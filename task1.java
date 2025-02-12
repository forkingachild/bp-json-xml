/*
 * this file is part of bp-json-xml project.
 *
 * Copyright (C) 2025 Hesam Tavakoli
 *
 * bp-json-xml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;

public class task1 {

    private static abstract class json_node {
        abstract int count();
    }

    private static class json_primitive extends json_node {
        Object value;
        json_primitive(Object value) {
            this.value = value;
        }
        @Override
        int count() {
            return 1;
        }
    }

    private static class json_array_node extends json_node {
        List<json_node> elements = new ArrayList<>();
        @Override
        int count() {
            int total = 1;
            for(json_node node : elements){
                total += node.count();
            }
            return total;
        }
    }

    private static class json_object_node extends json_node {
        Map<String, json_node> members = new HashMap<>();
        @Override
        int count() {
            int total = 1;
            for(json_node node : members.values()){
                total += node.count();
            }
            return total;
        }
    }

    private static class json_parser {
        private String s;
        private int index;
        json_parser(String s) {
            this.s = s;
            this.index = 0;
        }
        
        private void skip_whitespace() {
            while(index < s.length() && Character.isWhitespace(s.charAt(index))){
                index++;
            }
        }
        
        private char current_char() throws Exception {
            if(index >= s.length()){
                throw new Exception("unexpected end of input");
            }
            return s.charAt(index);
        }
        
        private void expect_char(char expected) throws Exception {
            skip_whitespace();
            if(index >= s.length() || s.charAt(index) != expected){
                throw new Exception("expected '" + expected + "' at position " + index);
            }
            index++;
        }
        
        json_node parse_value() throws Exception {
            skip_whitespace();
            if(index >= s.length()){
                throw new Exception("empty input");
            }
            char c = s.charAt(index);
            if(c == '{'){
                return parse_object();
            } else if(c == '['){
                return parse_array();
            } else if(c == '"'){
                return new json_primitive(parse_string());
            } else if(c == 't' || c == 'f' || c == 'n'){
                return new json_primitive(parse_literal());
            } else if(c == '-' || c == '.' || Character.isDigit(c)){
                return new json_primitive(parse_number());
            } else {
                throw new Exception("invalid character at position " + index + ": " + c);
            }
        }
        
        private json_node parse_object() throws Exception {
            expect_char('{');
            skip_whitespace();
            json_object_node obj = new json_object_node();
            if(index < s.length() && s.charAt(index) == '}'){
                index++;
                return obj;
            }
            while(true){
                skip_whitespace();
                if(index >= s.length() || s.charAt(index) != '"'){
                    throw new Exception("expected string key at position " + index);
                }
                String key = parse_string();
                skip_whitespace();
                expect_char(':');
                json_node value = parse_value();
                obj.members.put(key, value);
                skip_whitespace();
                if(index < s.length() && s.charAt(index) == ','){
                    index++;
                } else {
                    break;
                }
            }
            skip_whitespace();
            expect_char('}');
            return obj;
        }
        
        private json_node parse_array() throws Exception {
            expect_char('[');
            skip_whitespace();
            json_array_node array = new json_array_node();
            if(index < s.length() && s.charAt(index) == ']'){
                index++;
                return array;
            }
            while(true){
                json_node element = parse_value();
                array.elements.add(element);
                skip_whitespace();
                if(index < s.length() && s.charAt(index) == ','){
                    index++;
                } else {
                    break;
                }
            }
            skip_whitespace();
            expect_char(']');
            return array;
        }
        
        private String parse_string() throws Exception {
            expect_char('"');
            StringBuilder sb = new StringBuilder();
            while(index < s.length()){
                char c = s.charAt(index++);
                if(c == '"'){
                    return sb.toString();
                } else if(c == '\\'){
                    if(index >= s.length()){
                        throw new Exception("unexpected end of input in string escape");
                    }
                    char next = s.charAt(index++);
                    if(next == '"' || next == '\\'){
                        sb.append(next);
                    } else {
                        throw new Exception("invalid escape character: \\" + next);
                    }
                } else if(c == '\n' || c == '\r'){
                    throw new Exception("newline in string not allowed");
                } else {
                    if(c >= 32 && c <= 126){
                        sb.append(c);
                    } else {
                        throw new Exception("invalid character in string: " + c);
                    }
                }
            }
            throw new Exception("unterminated string");
        }
        
        private Number parse_number() throws Exception {
            skip_whitespace();
            int start = index;
            boolean negative = false;
            if(index < s.length() && s.charAt(index) == '-'){
                negative = true;
                index++;
            }
            boolean has_digit = false;
            boolean has_dot = false;
            if(index < s.length() && s.charAt(index) == '.'){
                has_dot = true;
                index++;
            }
            while(index < s.length() && Character.isDigit(s.charAt(index))){
                has_digit = true;
                index++;
            }
            if(index < s.length() && s.charAt(index) == '.'){
                if(has_dot){
                    throw new Exception("multiple dots in number at position " + index);
                }
                has_dot = true;
                index++;
                if(index >= s.length() || !Character.isDigit(s.charAt(index))){
                    throw new Exception("expected digit after dot at position " + index);
                }
                while(index < s.length() && Character.isDigit(s.charAt(index))){
                    has_digit = true;
                    index++;
                }
            }
            if(!has_digit){
                throw new Exception("invalid number format at position " + start);
            }
            String num_str = s.substring(start, index);
            try {
                if(has_dot){
                    return Double.parseDouble(num_str);
                } else {
                    return Long.parseLong(num_str);
                }
            } catch (NumberFormatException e) {
                throw new Exception("number format error: " + num_str);
            }
        }
        
        private Object parse_literal() throws Exception {
            skip_whitespace();
            if(s.startsWith("true", index)){
                index += 4;
                return Boolean.TRUE;
            } else if(s.startsWith("false", index)){
                index += 5;
                return Boolean.FALSE;
            } else if(s.startsWith("null", index)){
                index += 4;
                return null;
            } else {
                throw new Exception("invalid literal at position " + index);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = br.readLine();
        json_parser parser = new json_parser(input);
        json_node root = null;
        try {
            root = parser.parse_value();
            parser.skip_whitespace();
            if(parser.index != input.length()){
                System.out.println(0);
                return;
            }
        } catch (Exception e) {
            System.out.println(0);
            return;
        }
        if(!(root instanceof json_object_node)){
            System.out.println(0);
        } else {
            System.out.println(root.count());
        }
    }
}
