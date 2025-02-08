# XML task
the problem requires parsing a given **multi-line** string to check if it follows the **XML object (XMLObject)** format. if valid, the task is to count all **XML values (XMLValue)** within the object. Otherwise, return `0`.
    
---

## key points
* **valid XMLValue types:** `null` (empty tag), `boolean`, `integer`, `float`, `string`, `XMLObject`, `XMLArray`
* **XMLObject:** a key-value pair represented as `<key>value</key>`
* **XMLArray:** if multiple elements share the same key within an XMLObject, they form an ordered XMLArray
* **whitespace and indentation** are ignored
* **invalid cases:**
  * the input is not an XMLObject (e.g., standalone values, missing root tag)
  * malformed XML syntax (e.g., missing closing tags, unescaped special characters)

----

## input
multiple lines of a **single XML document** (max **1000 lines**, each max **1000 characters**)

## output
an **integer:**
* **number of XMLValues** inside the parsed XMLObject
* `0` **if input is invalid** (not a XMLObject)

---

## examples
  * ### testcase 1:
    * #### input:
      ```xml
      <d></d>
      ```
    * #### output:
      ```xml
      2
      ```
      (contains two XMLValues: `"d"` and an empty value)
  * ### testcase 2:
    * #### input:
      ```xml
      <base>
        <li>235</li>
        <li>235</li>
        <d></d>
      </base>
      ```
    * #### output:
      ```xml
      6
      ```
      (counts all values inside the object and arrays)
  * ### testcase 3:
    * #### input:
      ```xml
      <base>
        <a>"b"</a>
        <bd>true</bd>
        <ab>
            <a>false</a>
        </ab>
        <li>235</li>
        <li>235</li>
        <li>
            <akey>
                <d></d>
            </akey>
        </li>
      </base>
      ```
    * #### output:
      ```xml
      14
      ```
      (a nested structure with multiple values)
  * ### testcase 4 (invalid):
    * #### input:
      ```xml
      true
      ```
    * #### output:
      ```xml
      0
      ```
      (not a XMLObject)
  * ### testcase 5 (invalid):
    * #### input:
      ```xml
      <a></a>
      <b></b>
      ```
    * #### output:
      ```xml
      0
      ```
      (multiple root elements are not allowed)
  * ### testcase 6 (invalid):
    * #### input:
      ```xml
      <a>null</a>
      ```
    * #### output:
      ```xml
      0
      ```
      (not properly formatted XML)

---

this problem is similar to the JSON version but with **structured tag-based formatting** instead of key-value pairs with curly braces

