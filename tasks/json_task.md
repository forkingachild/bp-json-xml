# JSON task
the problem requires parsing a given string to check if it follows the **JSON object (JSONObject)** format. if valid, the task is to count all **JSON values (JSONValue)** within the object. otherwise, return `0`.

## key points
* **valid JSONValue types:** `null`, `boolean`, `integer`, `float`, `string`, `JSONObject`, `JSONArray`
* **JSONObject:** a set of key-value pairs where *keys are unique and strings*
* **JSONArray:** a list of JSONValues
* **invalid cases:**
  * the input is not a JSON object (`JSONObject`)
  * malformed JSON syntax (e.g., missing quotes for string keys, incorrect escape characters)
  * duplicate keys are resolved by keeping the last occurrence

## input
a **single string** (max length `10000`)

## output
an **integer:**
* **Number of JSONValues** inside the parsed JSONObject
* `0` **if input is invalid** (not a JSONObject)

## examples
  * ### testcase 1:
    * #### input:
      ```json
      {"d":null}
      ```
    * #### output:
      ```json
      2
      ```
      (contains two JSONValues: `"d"` and `null`)
  * ### testcase 2:
    * #### input:
      ```json
      {"li":[235,{},{"d":null}]}
      ```
    * #### output:
      ```json
      6
      ```
      (counts all values inside the object and arrays)
  * ### testcase 3:
    * #### input:
      ```json
      {"a":"b", "bd":true, "ab":{"a":false},"li":[235,{},{"d":null}]}
      ```
    * #### output:
      ```json
      10
      ```
      (a nested structure with multiple values)
  * ### testcase 4 (invalid):
    * #### input:
      ```
      true
      ```
    * #### output:
      ```json
      0
      ```
      (not a JSONObject)
  * ### testcase 5 (invalid):
    * #### input:
      ```
      [true]
      ```
    * #### output:
      ```json
      0
      ```
      (not a JSONObject)
  * ### testcase 6 (invalid):
    * #### input:
      ```
      [a:true]
      ```
    * #### output:
      ```json
      0
      ```
      (invalid key format—missing quotes)
  * ### testcase 7 (duplicate keys):
    * #### input:
      ```json
      {"a":true,"a":123,"a":[321,"b"],"a":[null]}
      ```
    * #### output:
      ```json
      3
      ```
      (only the last `"a"` key is considered)
  * ### testcase 8 (invalid string):
    * #### input:
      ```json
      {"a":"ab"cd"}
      ```
    * #### output:
      ```json
      0
      ```
      (incorrect string formatting)
  * ### testcase 9 (valid escaped string):
    * #### input:
      ```json
      {"a":"ab\"cd"}
      ```
    * #### output:
      ```json
      2
      ```
      (properly escaped quote inside the string)

