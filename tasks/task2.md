# XML standard format explanation
XML is another data storage standard that keeps data as a string, which can be parsed and retrieved in various programming languages. in this standard, data is stored in a recursive structure. however in this project we use a specific XML format. in this format we have three types of elements: `XMLArray`, `XMLObject` and `XMLValue`

---

## XMLValue:
an `XMLValue` can hold a value or multiple `XMLObject` and `XMLArray` elements (i will explain `XMLObject` and `XMLArray` later). in this project, values can be of five types: null, natural numbers, real numbers, boolean variables and strings. natural and real numbers are displayed in base-10
- `null` is represented as an empty value
- natural numbers are stored in `long` variables, and real numbers in `double` variables
- boolean variables can hold either `true` or `false`
- string values are displayed as `"value"` where `value` is a single-line ASCII printable string. however to represent four special characters `" < > \` in a string we use escape sequences: `\"`, `\<`, `\>` and `\\`
### examples:
```
true
false
2343266
.23423
324.234324

0.3242
"ssdkjfh"
"ggjufd/jh"
"ggju\>fd/jh"
```

---

## XMLObject and XMLArray:
an `XMLObject` consists of key-value pairs in the format `<key>value</key>` where `key` is a single word (without spaces) and `value` is an `XMLValue`. since values are accessed via their keys the order of key-value pairs with different keys is irrelevant. however if multiple key-value pairs with the same key appear in an `XMLObject` they form an `XMLArray` which maintains the order of appearance. the indices in an `XMLArray` are sequential, and the order of `XMLObject` elements inside it follows the order in the parent `XMLObject`
### examples:
  1. 
  ```xml
  <outter>"a"</outter>
  ```
  2. 
  ```xml
  <outter></outter>
  ```
  3. 
  ```xml
  <outter><a>"a"</a><b>"a"</b><b>"d"</b><a>"c"</a></outter>
  ```
  4. 
  ```xml
  <a><b>"ali"</b><c><d>"sara"</d><b><e>"hamid"</e></b></c></a>
  ```

---

## multi-line and indented representation:
since this XML format uses single-line strings, no additional separator characters are needed. when displaying XML in a multi-line format we assume that all lines are concatenated before parsing. also white-space characters outside string values do not affect the XML parsing and can be ignored. this allows XML to be presented in an indented and multi-line format for better readability
### examples:
  1. 
  ```xml
  <outter></outter>
  ```
  2. 
  ```xml
  <outter>
    <a>"a"</a>
    <b>"a"</b>
    <b>"d"</b>
    <a>"c"</a>
  </outter>
  ```
  3. 
  ```xml
  <outter>
    <a>"a"</a>
    <b>"a"</b>
    <b>"d"</b>
    <a>"c"</a>
  </outter>
  ```
  4. 
  ```xml
  <a>
    <b>"ali"</b>
    <c>
      <d>"sara"</d>
      <b>
        <e>"hamid"</e>
      </b>
    </c>
  </a>
  ```

---

## reading XML-formatted data:
in this task we need to read a multi-line text input, parse it as an XML-formatted data structure and count the total number of `XMLValue` elements. if the input is not a valid `XMLObject`,we should report an error

**input:** multiple lines of text each with a maximum length of `1000` characters. the number of lines does not exceed `1000`

**output:** print a single line with the total number of `XMLValue` elements found in the input. if the input is not a valid `XMLObject` print `0`

### examples:
  #### input 1:
  ```xml
  <d></d>
  ```
  #### output 1:
  ```
  2
  ```

  #### input 2:
  ```xml
  <base>
  <li>235</li>
  <li>235</li>
  <d></d>
  </base>
  ```
  #### output 2:
  ```
  6
  ```

  #### input 3:
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
  #### output 3:
  ```
  14
  ```

  #### input 4:
  ```xml
  true
  ```
  #### output 4:
  ```
  0
  ```

  #### input 5:
  ```xml
  <a></a>
  <b></b>
  ```
  #### output 5:
  ```
  0
  ```

  #### input 6:
  ```xml
  <a>null</a>
  ```
  #### output 6:
  ```
  0
  ```
