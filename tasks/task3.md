## measuring data differences:
in this part of the project the goal is to measure the difference between two data structures, each represented in either **XML** or **JSON** format. as explained in previous sections data representation in one format can be converted into the other. a **XMLObject** corresponds to a **JSONObject** and an **XMLValue** matches a **JSONValue**. however there are two key differences that challenge a one-to-one mapping between the formats:
1. **root element naming**: in XML the root element has a name, whereas in JSON there is no explicit root key
2. **array element naming**: in XML every element, including array elements, has a key. while in JSON array elements are indexed without names

these differences imply that an XML representation can always be converted to JSON, but not necessarily the other way around. to determine the similarity between two datasets both are first converted into a JSON-like tree structure and their differences are computed based on a specific metric that measures structural variation

---

### tree representation of data:
a dataset is represented as a **tree**, where:
- the **root node** corresponds to the top-level **JSONObject** that encapsulates the entire dataset
- each **JSONObject** or **JSONArray** corresponds to a **node** in the tree with the parent node being the **JSONObject** or **JSONArray** that contains it
- **leaf nodes** correspond to primitive values, empty objects (`{}`) or empty arrays (`[]`)

a key challenge in comparing two trees is the **order of child nodes**. to address this, each node is assigned a **name**:
- if a node corresponds to a **JSONValue** with a key `"key"` its name is `"key_"`
- if a node corresponds to a **JSONValue** inside an **array** at index `i`, its name is `"i"`, ensuring that sibling nodes always have unique names
- the children of each node are sorted **alphabetically** by their names

each **leaf node** has a **value**, which is:
- `[]` for an empty **JSONArray**,
- `{}` for an empty **JSONObject**,
- the actual stored value for primitive types

---

### measuring differences between two data trees:
to compute the **difference** between a **pair of datasets** (`original dataset` vs. `modified dataset`), the trees corresponding to both representations are compared. the difference is defined as the **minimum number of operations** required to transform one tree into the other

two trees are considered **equivalent** if there exists a **one-to-one mapping** between their nodes such that:
- mapped nodes have the **same name** and **same value**
- their **parents** are also mapped to the same parent node
- the **root node’s name is ignored** (it can differ)

### allowed operations:
to transform one tree into another the following operations are allowed:
- **add a new node** with an arbitrary name and empty value at the end of a sorted child list provided that alphabetical order is maintained.
- **delete a leaf node**, but only if it is the last child in its parent's sorted list
- **rename a node**, ensuring that alphabetical order is preserved
- **modify a node’s value**.

---

### input format:
the input consists of two datasets (a **pair of values**) separated by a line containing `---`. 
- the **first dataset** represents the **final state**
- the **second dataset** represents the **initial state**
- each dataset is either in **JSONObject** or **XMLObject** format
- the number of lines and the length of each line do not exceed **1000**

### output format:
output a **single integer** representing the computed difference between the **second dataset** (original) and the **first dataset** (final)

---

### examples:

#### **example 1**
##### **input:**
```
<base>
  <b></b>
</base>
---
{"b":null}
```
##### **output:**
```
0
```

#### **example 2**
##### **input:**
```
<abcd>
  <b></b>
</abcd>
---
<a>
  <b></b>
</a>
```
##### **output:**
```
0
```

#### **example 3**
##### **input:**
```
{"a":"first","b":"second"}
---
<root>
  <a>"first"</a>
  <b>"second"</b>
</root>
```
##### **output:**
```
0
```

#### **example 4**
##### **input:**
```
{"b":"second","a":"first"}
---
{"a":"first","b":"second"}
```
##### **output:**
```
0
```

#### **example 5**
##### **input:**
```
{"b":"second","a":"first"}
---
<root>
  <a>"first"</a>
</root>
```
##### **output:**
```
2
```

#### **example 6**
##### **input:**
```
{"b":"second","a":"first"}
---
<root>
  <a>"second"</a>
</root>
```
##### **output:**
```
3
```

#### **example 7**
##### **input:**
```
{"b":"second","a":"first"}
---
<root>
  <b>"second"</b>
</root>
```
##### **output:**
```
4
```

#### **example 8**
##### **input:**
```
{"a":null,"b":["first","second"]}
---
<root>
  <b>"first"</b>
  <b>"second"</b>
  <a></a>
</root>
```
##### **output:**
```
0
```

#### **example 9**
##### **input:**
```
{"a":null,"b":["second","first"]}
---
<root>
  <b>"first"</b>
  <b>"second"</b>
  <a></a>
</root>
```
##### **output:**
```
2
```

#### **example 10**
##### **input:**
```
{"a":null,"b":["first"]}
---
<root>
  <b>"first"</b>
  <a></a>
</root>
```
##### **output:**
```
3
```

#### **example 11**
##### **input:**
```
{"a":null,"b":[["second","first"]]}
---
<root>
  <b>"first"</b>
  <b>"second"</b>
  <a></a>
</root>
```
##### **output:**
```
6
```

