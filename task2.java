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

public class task2 {

    private static class xml_element {
        String tag;
        boolean is_primitive;
        int full_count;
        xml_element(String tag, boolean is_primitive, int full_count) {
            this.tag = tag;
            this.is_primitive = is_primitive;
            this.full_count = full_count;
        }
    }

    private static class xml_parser {
        String s;
        int index;
        xml_parser(String s) {
            this.s = s;
            this.index = 0;
        }

        private void skip_whitespace() {
            while(index < s.length() && Character.isWhitespace(s.charAt(index))){
                index++;
            }
        }

        private void expect_char(char c) throws Exception {
            skip_whitespace();
            if(index >= s.length() || s.charAt(index) != c){
                throw new Exception("expected '" + c + "' at position " + index);
            }
            index++;
        }

        private void expect(String str) throws Exception {
            skip_whitespace();
            if(index + str.length() > s.length() || !s.substring(index, index + str.length()).equals(str)){
                throw new Exception("expected \"" + str + "\" at position " + index);
            }
            index += str.length();
        }

        private String parse_tag_name() throws Exception {
            skip_whitespace();
            int start = index;
            while(index < s.length()){
                char c = s.charAt(index);
                if(Character.isWhitespace(c) || c == '>' || c == '/') break;
                index++;
            }
            if(start == index){
                throw new Exception("empty tag name at position " + index);
            }
            return s.substring(start, index);
        }

        private String parse_text() {
            int start = index;
            while(index < s.length() && s.charAt(index) != '<'){
                index++;
            }
            return s.substring(start, index);
        }

        private String parse_quoted_string() throws Exception {
            expect_char('"');
            StringBuilder sb = new StringBuilder();
            while(true){
                if(index >= s.length()){
                    throw new Exception("unterminated quoted string");
                }
                char c = s.charAt(index);
                if(c == '"'){
                    index++;
                    break;
                } else if(c == '\\'){
                    index++;
                    if(index >= s.length()){
                        throw new Exception("unterminated escape in quoted string");
                    }
                    char next = s.charAt(index);
                    if(next == '"' || next == '\\' || next == '<' || next == '>'){
                        sb.append('\\').append(next);
                    } else {
                        throw new Exception("invalid escape sequence in quoted string");
                    }
                    index++;
                } else {
                    sb.append(c);
                    index++;
                }
            }
            return "\"" + sb.toString() + "\"";
        }

        xml_element parse_element() throws Exception {
            skip_whitespace();
            if(index >= s.length() || s.charAt(index) != '<'){
                throw new Exception("expected '<' at position " + index);
            }
            expect_char('<');
            String tag = parse_tag_name();
            skip_whitespace();
            expect_char('>');
            skip_whitespace();
            boolean has_child_element = false;
            if(index < s.length() && s.charAt(index) == '<'){
                if(index + 1 < s.length() && s.charAt(index + 1) != '/'){
                    has_child_element = true;
                }
            }
            int value_count = 0;
            boolean is_primitive = false;
            if(has_child_element){
                List<xml_element> children = new ArrayList<>();
                while(true){
                    skip_whitespace();
                    if(index < s.length() && s.charAt(index) == '<' && (index+1 < s.length() && s.charAt(index+1) == '/')){
                        break;
                    }
                    if(index < s.length() && s.charAt(index) != '<'){
                        String txt = parse_text();
                        if(!txt.trim().isEmpty()){
                            throw new Exception("non-whitespace text in element <" + tag + ">");
                        }
                        continue;
                    }
                    xml_element child = parse_element();
                    children.add(child);
                }
                Map<String, List<xml_element>> groups = new LinkedHashMap<>();
                for(xml_element child : children){
                    groups.computeIfAbsent(child.tag, k -> new ArrayList<>()).add(child);
                }
                for(List<xml_element> group : groups.values()){
                    if(group.size() == 1){
                        value_count += group.get(0).full_count;
                    } else {
                        int group_count = group.get(0).full_count;
                        for(int i = 1;i < group.size();i++){
                            group_count += (group.get(i).full_count - 1);
                        }
                        value_count += group_count;
                    }
                }
                is_primitive = false;
            } else {
                skip_whitespace();
                String text;
                if(index < s.length() && s.charAt(index) == '"'){
                    text = parse_quoted_string();
                } else {
                    text = parse_text();
                }
                String trimmed = text.trim();
                if(!trimmed.isEmpty()){
                    if(!(is_valid_boolean(trimmed) || is_valid_number(trimmed) || is_valid_string(trimmed))){
                        throw new Exception("invalid primitive value in tag <" + tag + ">: " + trimmed);
                    }
                }
                is_primitive = true;
            }
            skip_whitespace();
            expect("</");
            String end_tag = parse_tag_name();
            if(!end_tag.equals(tag)){
                throw new Exception("mismatched tag: expected </" + tag + ">, got </" + end_tag + ">");
            }
            skip_whitespace();
            expect_char('>');
            int full_count = is_primitive ? 2 : (1 + value_count);
            return new xml_element(tag, is_primitive, full_count);
        }

        private boolean is_valid_boolean(String s) {
            return s.equals("true") || s.equals("false");
        }

        private boolean is_valid_number(String s) {
            return s.matches("^-?((\\d+\\.\\d*)|(\\.\\d+)|(\\d+))$");
        }

        private boolean is_valid_string(String s) {
            if(s.length() < 2)return false;
            if(s.charAt(0) != '"' || s.charAt(s.length() - 1) != '"')return false;
            for(int i = 1;i < s.length() - 1;i++){
                char c = s.charAt(i);
                if(c == '\\'){
                    i++;
                    if(i >= s.length() - 1)return false;
                    char next = s.charAt(i);
                    if(next != '"' && next != '\\' && next != '<' && next != '>')return false;
                } else {
                    if(c == '"' || c == '<' || c == '>' || c == '\\'){
                        return false;
                    }
                    if(c < 32 || c > 126)return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null){
            sb.append(line).append("\n");
        }
        String input = sb.toString();
        if(input.trim().isEmpty()){
            System.out.println(0);
            return;
        }
        xml_parser parser = new xml_parser(input);
        xml_element root;
        try{
            root = parser.parse_element();
            parser.skip_whitespace();
            if(parser.index != parser.s.length()){
                System.out.println(0);
                return;
            }
        } catch(Exception e){
            System.out.println(0);
            return;
        }
        System.out.println(root.full_count);
    }
}
