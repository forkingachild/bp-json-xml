# standard JSON format explanation
the JSON standard stores data as a string which can be parsed and converted back into its original data form in different programming languages. in this standard, data is stored in a structure that is recursively defined. this structure consists of three possible elements: **JSONObject**, **JSONArray** and **JSONValue**

---

## JSONValue
a **JSONValue** can store either a value, a **JSONObject** or a **JSONArray** (i'll explain what **JSONObject** and **JSONArray** are shortly). in this project we assume that values can be of five types: null, natural numbers, real numbers, boolean values and strings. natural and real numbers are represented in base-10 notation
  * **null** is represented as `null`
  * **natural numbers** are stored as `long` variables and **real numbers** are stored as `double` variables
  * **boolean values** can take either `true` or `false`
  * **string values** are displayed as `"value"` where `value` is a single-line ASCII string containing printable characters
    * an exception exists for the characters `"` and `\`:
       * `"` is represented as `\"`
       * `\` is represented as `\\`
    * (in standard JSON all characters can appear in a string but for simplicity we impose these restrictions)
### examples:
```json
true  
false  
2343266  
.23423  
324.234324  
0.3242  
null  
"ssdkjfh"  
"ssdk\"jfh"  
"ssdk\\jfh"  
[true,{"a":"b", "bd":true, "ab":{"a":false},"li":[235]},null,3455]  
{"a":"b", "bd":true, "ab":{"a":false},"li":[235,{},{"d":null}]}  
```

---

## JSONArray
a **JSONArray** is a list of multiple **JSONValue** elements. to represent a list, its elements are separated by a `,`, enclosed within square brackets `[ ]`
### example:
```json
[true,{"a":"b", "bd":true, "ab":{"a":false},"li":[235]},null,3455]  
```

---

## JSONObject
a **JSONObject** consists of multiple key-value pairs, where:
  * the **key** is always a string
  * the **value** is a **JSONValue**
since values are accessed using their keys if a key appears multiple times within a **JSONObject** only the last key-value pair is considered and the previous ones are ignored (see sample input/output 7). therefore the order of key-21value pairs within a **JSONObject** does not matter.
to represent a **JSONObject** key-value pairs are separated by a `,` enclosed within curly braces `{ }`
### example:
```json
{"a":"b", "bd":true, "ab":{"a":false},"li":[235,{},{"d":null}]}  
```

---

## reading JSON-formatted data
in this problem we need to write a program that given a string input, parses it as a **JSONObject** and counts the total number of **JSONValue** elements appearing in it.  
if the input is **not** a valid **JSONObject** the program should return `0`

---

## input:
a single line containing a string of at most **10,000** characters

## output:
print the total number of **JSONValue** elements present in the input.  
if the input is not a valid **JSONObject** print `0`

## examples:
### input 1:
```json
{"d":null}  
```
### output 1:
```json
2
```

### input 2:
```json
{"li":[235,{},{"d":null}]}  
```
### output 2:
```json
6
```

### input 3:
```json
{"a":"b", "bd":true, "ab":{"a":false},"li":[235,{},{"d":null}]}  
```
### output 3:
```json
10
```

### input 4:
```json
true
```
### output 4:
```json
0
```

### input 5:
```json
[true]
```
### output 5:
```json
0
```

### input 6:
```json
{a:true}
```
### output 6:
```json
0
```

### input 7:
```json
{"a":true,"a":123,"a":[321,"b"],"a":[null]}  
```
### output 7:
```json
3
```

### input 8:
```json
{"a":"ab"cd"}  
```
### output 8:
```json
0
```

### input 9:
```json
{"a":"ab\"cd"}
```
### output 9:
```json
2
```