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

public class diffs_count {

    static final int inf = 1000000;

    static class tree_node {
        String name;
        boolean is_leaf;
        String value;
        List<tree_node> children;

        tree_node(String name, boolean is_leaf, String value) {
            this.name = name;
            this.is_leaf = is_leaf;
            this.value = value;
            this.children = new ArrayList<>();
        }
    }

    static class json_parser {
        String s;
        int index;

        json_parser(String s) {
            this.s = s;
            this.index = 0;
        }

        void skip_whitespace() throws Exception {
            while(index < s.length() && Character.isWhitespace(s.charAt(index))) {
                index++;
            }
        }

        Object parse_value() throws Exception {
            skip_whitespace();
            if(index >= s.length())
                throw new Exception("unexpected end");
            char c = s.charAt(index);
            if(c == '{') 
                return parse_object();
            if(c == '[') 
                return parse_array();
            if(c == '"') 
                return parse_string();
            if(c == 't' || c == 'f' || c == 'n') 
                return parse_literal();
            if(c == '-' || Character.isDigit(s.charAt(index)) || c == '.')
                return parse_number();
            throw new Exception("unexpected character: " + c);
        }

        Map<String, Object> parse_object() throws Exception {
            Map<String, Object> map = new LinkedHashMap<>();
            index++; // skip '{'
            skip_whitespace();
            if(index < s.length() && s.charAt(index) == '}') {
                index++;
                return map;
            }
            while(true) {
                skip_whitespace();
                if(s.charAt(index) != '"')
                    throw new Exception("expected '\"'");
                String key = parse_string();
                skip_whitespace();
                if(index >= s.length() || s.charAt(index) != ':')
                    throw new Exception("expected ':'");
                index++; // skip ':'
                Object val = parse_value();
                map.put(key, val);
                skip_whitespace();
                if(index >= s.length())
                    throw new Exception("expected '}'");
                char ch = s.charAt(index);
                index++;
                if(ch == '}') {
                    break;
                }
                if(ch != ',')
                    throw new Exception("expected ','");
            }
            return map;
        }

        List<Object> parse_array() throws Exception {
            List<Object> list = new ArrayList<>();
            index++; // skip '['
            skip_whitespace();
            if(index < s.length() && s.charAt(index) == ']') {
                index++;
                return list;
            }
            while(true) {
                Object val = parse_value();
                list.add(val);
                skip_whitespace();
                if(index >= s.length())
                    throw new Exception("expected ']'");
                char ch = s.charAt(index);
                index++;
                if(ch == ']') {
                    break;
                }
                if(ch != ',')
                    throw new Exception("expected ','");
            }
            return list;
        }

        String parse_string() throws Exception {
            StringBuilder sb = new StringBuilder();
            index++; // skip opening quote
            while(true) {
                if(index >= s.length())
                    throw new Exception("unterminated string");
                char c = s.charAt(index++);
                if(c == '"') {
                    break;
                }
                if(c == '\\') {
                    if(index >= s.length())
                        throw new Exception("unterminated escape");
                    char next = s.charAt(index++);
                    if(next == '"' || next == '\\' || next == '/' || next == 'b' ||
                       next == 'f' || next == 'n' || next == 'r' || next == 't') {
                        sb.append(next);
                    } else {
                        throw new Exception("invalid escape");
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        Object parse_number() throws Exception {
            int start = index;
            if(s.charAt(index) == '-')
                index++;
            while(index < s.length() && Character.isDigit(s.charAt(index))) {
                index++;
            }
            if(index < s.length() && s.charAt(index) == '.') {
                index++;
                while(index < s.length() && Character.isDigit(s.charAt(index))) {
                    index++;
                }
            }
            String num_str = s.substring(start, index);
            if(num_str.indexOf('.') >= 0)
                return Double.parseDouble(num_str);
            return Long.parseLong(num_str);
        }

        Object parse_literal() throws Exception {
            if(s.startsWith("true", index)) {
                index += 4;
                return Boolean.TRUE;
            }
            if(s.startsWith("false", index)) {
                index += 5;
                return Boolean.FALSE;
            }
            if(s.startsWith("null", index)) {
                index += 4;
                return null;
            }
            throw new Exception("invalid literal");
        }
    }

    static tree_node build_tree_from_json(Object json, String name) {
        if(json instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) json;
            if(map.isEmpty()) {
                return new tree_node(name, true, "{}");
            } else {
                tree_node node = new tree_node(name, false, null);
                List<tree_node> children = new ArrayList<>();
                for(Map.Entry<String, Object> e : map.entrySet()) {
                    String child_name = e.getKey() + "_";
                    children.add(build_tree_from_json(e.getValue(), child_name));
                }
                Collections.sort(children, Comparator.comparing(n -> n.name));
                node.children = children;
                return node;
            }
        } else if(json instanceof List) {
            List<Object> list = (List<Object>) json;
            if(list.isEmpty()) {
                return new tree_node(name, true, "[]");
            } else {
                tree_node node = new tree_node(name, false, null);
                List<tree_node> children = new ArrayList<>();
                for(int i = 0; i < list.size(); i++) {
                    String child_name = String.valueOf(i);
                    children.add(build_tree_from_json(list.get(i), child_name));
                }
                node.children = children;
                return node;
            }
        } else {
            String val = (json == null ? "" : json.toString());
            return new tree_node(name, true, val);
        }
    }

    static class xml_element_raw {
        String tag;
        String text;
        List<xml_element_raw> children;

        xml_element_raw(String tag) {
            this.tag = tag;
            this.text = "";
            this.children = new ArrayList<>();
        }
    }

    static class xml_parser {
        String s;
        int index;

        xml_parser(String s) {
            this.s = s;
            this.index = 0;
        }

        void skip_whitespace() {
            while(index < s.length() && Character.isWhitespace(s.charAt(index))) {
                index++;
            }
        }

        String parse_tag_name() throws Exception {
            skip_whitespace();
            int start = index;
            while(index < s.length() && !Character.isWhitespace(s.charAt(index)) &&
                  s.charAt(index) != '>' && s.charAt(index) != '/') {
                index++;
            }
            if(start == index)
                throw new Exception("empty tag name");
            return s.substring(start, index);
        }

        xml_element_raw parse_element() throws Exception {
            skip_whitespace();
            if(index >= s.length() || s.charAt(index) != '<')
                throw new Exception("expected '<' at position " + index);
            index++; // skip '<'
            String tag = parse_tag_name();
            skip_whitespace();
            while(index < s.length() && s.charAt(index) != '>' && s.charAt(index) != '/') {
                index++;
            }
            boolean self_closing = false;
            if(index < s.length() && s.charAt(index) == '/') {
                self_closing = true;
                index++; // skip '/'
            }
            if(index >= s.length() || s.charAt(index) != '>')
                throw new Exception("expected '>'");
            index++; // skip '>'
            xml_element_raw elem = new xml_element_raw(tag);
            if(self_closing) {
                return elem;
            }
            while(true) {
                skip_whitespace();
                if(index < s.length() && s.charAt(index) == '<') {
                    if(s.startsWith("</", index))
                        break;
                    xml_element_raw child = parse_element();
                    elem.children.add(child);
                } else {
                    int start = index;
                    while(index < s.length() && s.charAt(index) != '<') {
                        index++;
                    }
                    elem.text += s.substring(start, index);
                }
            }
            if(!s.startsWith("</", index))
                throw new Exception("expected closing tag for " + tag);
            index += 2; // skip "</"
            String end_tag = parse_tag_name();
            if(!end_tag.equals(tag))
                throw new Exception("mismatched tag: " + tag + " vs " + end_tag);
            skip_whitespace();
            if(index < s.length() && s.charAt(index) == '>')
                index++;
            return elem;
        }

        xml_element_raw parse() throws Exception {
            skip_whitespace();
            return parse_element();
        }
    }

    static String unquote(String s) {
        if(s.length() >= 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
            StringBuilder sb = new StringBuilder();
            for(int i = 1; i < s.length() - 1; i++) {
                char c = s.charAt(i);
                if(c == '\\' && i + 1 < s.length() - 1) {
                    i++;
                    char next = s.charAt(i);
                    sb.append(next);
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return s;
    }

    static tree_node build_tree_from_xml(xml_element_raw elem, boolean is_root) {
        if(is_root) {
            tree_node root = new tree_node("", false, null);
            Map<String, List<xml_element_raw>> groups = new TreeMap<>();
            for(xml_element_raw child : elem.children) {
                groups.computeIfAbsent(child.tag, k -> new ArrayList<>()).add(child);
            }
            List<tree_node> children = new ArrayList<>();
            for(Map.Entry<String, List<xml_element_raw>> entry : groups.entrySet()) {
                String tag = entry.getKey();
                List<xml_element_raw> group = entry.getValue();
                if(group.size() == 1) {
                    tree_node child_node = build_tree_from_xml(group.get(0), false);
                    child_node.name = tag + "_";
                    children.add(child_node);
                } else {
                    tree_node array_node = new tree_node(tag + "_", false, null);
                    List<tree_node> array_children = new ArrayList<>();
                    for(int i = 0; i < group.size(); i++) {
                        tree_node child_node = build_tree_from_xml(group.get(i), false);
                        child_node.name = String.valueOf(i);
                        array_children.add(child_node);
                    }
                    array_node.children = array_children;
                    children.add(array_node);
                }
            }
            Collections.sort(children, Comparator.comparing(n -> n.name));
            root.children = children;
            return root;
        } else {
            if(elem.children.isEmpty()) {
                String txt = elem.text.trim();
                if(!txt.isEmpty() && txt.charAt(0) == '"' && txt.charAt(txt.length() - 1) == '"') {
                    txt = unquote(txt);
                }
                return new tree_node(elem.tag + "_", true, txt);
            } else {
                tree_node node = new tree_node(elem.tag + "_", false, null);
                Map<String, List<xml_element_raw>> groups = new TreeMap<>();
                for(xml_element_raw child : elem.children) {
                    groups.computeIfAbsent(child.tag, k -> new ArrayList<>()).add(child);
                }
                List<tree_node> children = new ArrayList<>();
                for(Map.Entry<String, List<xml_element_raw>> entry : groups.entrySet()) {
                    String tag = entry.getKey();
                    List<xml_element_raw> group = entry.getValue();
                    if(group.size() == 1) {
                        tree_node child_node = build_tree_from_xml(group.get(0), false);
                        child_node.name = tag + "_";
                        children.add(child_node);
                    } else {
                        tree_node array_node = new tree_node(tag + "_", false, null);
                        List<tree_node> array_children = new ArrayList<>();
                        for(int i = 0; i < group.size(); i++) {
                            tree_node child_node = build_tree_from_xml(group.get(i), false);
                            child_node.name = String.valueOf(i);
                            array_children.add(child_node);
                        }
                        array_node.children = array_children;
                        children.add(array_node);
                    }
                }
                Collections.sort(children, Comparator.comparing(n -> n.name));
                node.children = children;
                return node;
            }
        }
    }

    static int diff(tree_node a, tree_node b, boolean ignore_name) {
        if(a.is_leaf && b.is_leaf) {
            int cost = 0;
            if(!ignore_name && !a.name.equals(b.name)) {
                cost += 1;
            }
            if(!normalize(a.value).equals(normalize(b.value))) {
                cost += 1;
            }
            return cost;
        } else if(!a.is_leaf && !b.is_leaf) {
            int cost = 0;
            if(!ignore_name && !a.name.equals(b.name)) {
                cost += 1;
            }
            cost += children_diff(a.children, b.children);
            return cost;
        } else {
            if(a.is_leaf && !b.is_leaf) {
                int ins = insertion_cost(b.children);
                if(ins >= inf)
                    return inf;
                int cost = 1 + ins;
                if(!ignore_name && !a.name.equals(b.name)) {
                    cost += 1;
                }
                return cost;
            } else {
                int del = deletion_cost(a.children);
                if(del >= inf)
                    return inf;
                int cost = 1 + del;
                if(!ignore_name && !a.name.equals(b.name)) {
                    cost += 1;
                }
                return cost;
            }
        }
    }

    static int insertion_cost(List<tree_node> list) {
        int cost = 0;
        for(tree_node node : list) {
            if(!node.is_leaf)
                return inf;
            cost += 2;
        }
        return cost;
    }

    static int deletion_cost(List<tree_node> list) {
        int cost = 0;
        for(tree_node node : list) {
            if(!node.is_leaf)
                return inf;
            cost += 1;
        }
        return cost;
    }

    static int children_diff(List<tree_node> A, List<tree_node> B) {
        int m = A.size(), n = B.size();
        int cost = 0;
        int k = Math.min(m, n);
        for(int i = 0; i < k; i++) {
            cost += diff(A.get(i), B.get(i), false);
            if(cost >= inf)
                return inf;
        }
        if(m < n) {
            for(int i = m; i < n; i++) {
                if(!B.get(i).is_leaf)
                    return inf;
                cost += 2;
            }
        } else if(m > n) {
            for(int i = n; i < m; i++) {
                if(!A.get(i).is_leaf)
                    return inf;
                cost += 1;
            }
        }
        return cost;
    }

    static String normalize(String s) {
        if(s == null)
            return "";
        if(s.equals("null"))
            return "";
        return s;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<String> all_lines = new ArrayList<>();
        String line;
        while((line = br.readLine()) != null) {
            all_lines.add(line);
        }
        if(all_lines.isEmpty()) {
            System.out.println(0);
            return;
        }
        int sep = -1;
        for(int i = 0; i < all_lines.size(); i++) {
            if(all_lines.get(i).trim().equals("---")) {
                sep = i;
                break;
            }
        }
        if(sep == -1) {
            System.out.println(0);
            return;
        }
        StringBuilder sb_final = new StringBuilder();
        for(int i = 0; i < sep; i++) {
            sb_final.append(all_lines.get(i)).append("\n");
        }
        StringBuilder sb_orig = new StringBuilder();
        for(int i = sep + 1; i < all_lines.size(); i++) {
            sb_orig.append(all_lines.get(i)).append("\n");
        }
        String dataset_final = sb_final.toString().trim();
        String dataset_orig = sb_orig.toString().trim();
        tree_node final_tree = null, orig_tree = null;
        try {
            if(dataset_final.startsWith("<")) {
                xml_parser xp = new xml_parser(dataset_final);
                xml_element_raw xml_root = xp.parse();
                final_tree = build_tree_from_xml(xml_root, true);
            } else {
                json_parser jp = new json_parser(dataset_final);
                Object json = jp.parse_value();
                final_tree = build_tree_from_json(json, "");
            }
            if(dataset_orig.startsWith("<")) {
                xml_parser xp = new xml_parser(dataset_orig);
                xml_element_raw xml_root = xp.parse();
                orig_tree = build_tree_from_xml(xml_root, true);
            } else {
                json_parser jp = new json_parser(dataset_orig);
                Object json = jp.parse_value();
                orig_tree = build_tree_from_json(json, "");
            }
        } catch(Exception e) {
            System.out.println(0);
            return;
        }
        int answer = children_diff(orig_tree.children, final_tree.children);
        System.out.println(answer >= inf ? 0 : answer);
    }
}
