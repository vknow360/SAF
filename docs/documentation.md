> <h2> Docs for: SAF</h2> 

> <h2> Events </h2> 

 > <h3>DocumentCreated</h3>Event invoked after creating document.Returns document's uri if operation was successful else returns error message
Params           |  []()       
---------------- | ------- 

```` uriString | text````

 ____________________________________

> <h3>GotCopyResult</h3>Event invoked after getting copy document result.Response will be target document's uri if operation was successful else returns error message
Params           |  []()       
---------------- | ------- 

```` successful | boolean````
```` response | text````

 ____________________________________

> <h3>GotFilesList</h3>Event invoked after getting files list
Params           |  []()       
---------------- | ------- 

```` filesList | list````

 ____________________________________

> <h3>GotMoveResult</h3>Event invoked after getting move document result.Response will be target document's uri if operation was successful else returns error message
Params           |  []()       
---------------- | ------- 

```` successful | boolean````
```` response | text````

 ____________________________________

> <h3>GotReadResult</h3>Event invoked after reading from document.Returns content if operation was successful else returns error message
Params           |  []()       
---------------- | ------- 

```` result | text````

 ____________________________________

> <h3>GotUri</h3>Event invoked when user selects a document or tree from SAF file picker
Params           |  []()       
---------------- | ------- 

```` uri | any````
```` uriString | text````

 ____________________________________

> <h3>GotWriteResult</h3>Event invoked after writing to document.Returns document's uri if operation was successful else returns error message
Params           |  []()       
---------------- | ------- 

```` response | text````

 ____________________________________

> <h2> Methods </h2> 

 > <h3>BuildChildDocumentsUriUsingTree</h3>Builds child documents id using tree (documents which is child of parent document) uri and its parent document id
Params           |  []()       
---------------- | ------- 

```` treeUri | text````<br>
```` parentDocumentId | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>BuildDocumentUriUsingTree</h3>Builds document uri using tree uri and document id
Params           |  []()       
---------------- | ------- 

```` treeUri | text````<br>
```` documentId | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>CopyDocument</h3>Tries to copy document from source uri to target dir
Params           |  []()       
---------------- | ------- 

```` sourceUri | text````<br>
```` targetParentUri | text````<br>

____________________________________

> <h3>CreateDocument</h3>Creates a new and empty document.If document already exists then an incremental value will be suffixed.
Params           |  []()       
---------------- | ------- 

```` parentDocumentUri | text````<br>
```` fileName | text````<br>
```` mimeType | text````<br>

____________________________________

> <h3>CreateFlags</h3>Combines two flags and returns resulting flag
Params           |  []()       
---------------- | ------- 

```` f1 | number````<br>
```` f2 | number````<br>

<i>Return type : number</i>

____________________________________

> <h3>DeleteDocument</h3>Tries to delete document from given uri and returns result
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

<i>Return type : boolean</i>

____________________________________

> <h3>GetDisplayName</h3>Returns display name of given document uri
Params           |  []()       
---------------- | ------- 

```` documentUri | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>GetDocumentId</h3>Returns document id of an uri (should only be grand child)
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>GetLastModifiedTime</h3>Returns last modified time (epoch) of given document uri
Params           |  []()       
---------------- | ------- 

```` documentUri | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>GetMimeType</h3>Returns mime type of given document uri
Params           |  []()       
---------------- | ------- 

```` documentUri | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>GetSize</h3>Returns size (in bytes) of given document uri
Params           |  []()       
---------------- | ------- 

```` documentUri | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>GetTreeDocumentId</h3>Returns document id of tree uri (should be either tree uri itself or a direct child uri)
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>IsChildDocumentUri</h3>Returns whether second uri is child of first uri
Params           |  []()       
---------------- | ------- 

```` parentUri | text````<br>
```` childUri | text````<br>

<i>Return type : boolean</i>

____________________________________

> <h3>IsDocumentUri</h3>Returns whether given uri is a document uri
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

<i>Return type : boolean</i>

____________________________________

> <h3>IsReadGranted</h3>Returns whether read is available for given uri
Params           |  []()       
---------------- | ------- 

```` uri | text````<br>

<i>Return type : boolean</i>

____________________________________

> <h3>IsTreeUri</h3>Returns whether given uri is a tree uri
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

<i>Return type : boolean</i>

____________________________________

> <h3>IsWriteGranted</h3>Returns whether write is available for given uri
Params           |  []()       
---------------- | ------- 

```` uri | text````<br>

<i>Return type : boolean</i>

____________________________________

> <h3>ListFiles</h3>Tries to list files from given dir
Params           |  []()       
---------------- | ------- 

```` dirUri | text````<br>
```` dirDocumentId | text````<br>

____________________________________

> <h3>MoveDocument</h3>Tries to move document from source uri to target dir
Params           |  []()       
---------------- | ------- 

```` sourceUri | text````<br>
```` sourceParentUri | text````<br>
```` targetParentUri | text````<br>

____________________________________

> <h3>OpenDocumentTree</h3>Prompts user to select a document tree
Params           |  []()       
---------------- | ------- 

```` title | text````<br>
```` initialDir | text````<br>

____________________________________

> <h3>OpenSingleDocument</h3>Prompts user to select a single file
Params           |  []()       
---------------- | ------- 

```` title | text````<br>
```` category | text````<br>
```` type | text````<br>
```` extraMimeTypes | list````<br>

____________________________________

> <h3>ReadFromFile</h3>Reads from given uri
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

____________________________________

> <h3>ReleasePermission</h3>Relinquish a persisted URI permission grant
Params           |  []()       
---------------- | ------- 

```` uri | text````<br>
```` flags | number````<br>

____________________________________

> <h3>RenameDocument</h3>Tries to rename a document and returns updated uri
Params           |  []()       
---------------- | ------- 

```` documentUri | text````<br>
```` displayName | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>StringFromUriObject</h3>Convert uri to string
Params           |  []()       
---------------- | ------- 

```` uri | any````<br>

<i>Return type : text</i>

____________________________________

> <h3>StringToUriObject</h3>Converts string to uri
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

<i>Return type : any</i>

____________________________________

> <h3>TakePersistableUriPermission</h3>Take a persistable URI permission grant that has been offered. Once taken, the permission grant will be remembered across device reboots.
Params           |  []()       
---------------- | ------- 

```` uri | any````<br>
```` flags | number````<br>

____________________________________

> <h3>WriteToFile</h3>Writes to given uri
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>
```` content | text````<br>

____________________________________

> <h2> Properties </h2> 

 > <h3>DocumentDirMimeType</h3>Returns mime type of document dir
<i>Property Type : read-only</i><br><i>Accepts : text</i>
____________________________________

> <h3>FlagGrantReadPermission</h3>Flag to get write permission
<i>Property Type : read-only</i><br><i>Accepts : number</i>
____________________________________

> <h3>FlagGrantWritePermission</h3>Flag to get read permission
<i>Property Type : read-only</i><br><i>Accepts : number</i>
____________________________________

> <h2> Docs for: SAF</h2> 

> <h2> Events </h2> 

 > <h3>DocumentCreated</h3>Event invoked after creating document.Returns document's uri if operation was successful else returns error message
Params           |  []()       
---------------- | ------- 

```` uriString | text````

 ____________________________________

> <h3>GotCopyResult</h3>Event invoked after getting copy document result.Response will be target document's uri if operation was successful else returns error message
Params           |  []()       
---------------- | ------- 

```` successful | boolean````
```` response | text````

 ____________________________________

> <h3>GotFilesList</h3>Event invoked after getting files list
Params           |  []()       
---------------- | ------- 

```` filesList | list````

 ____________________________________

> <h3>GotMoveResult</h3>Event invoked after getting move document result.Response will be target document's uri if operation was successful else returns error message
Params           |  []()       
---------------- | ------- 

```` successful | boolean````
```` response | text````

 ____________________________________

> <h3>GotReadResult</h3>Event invoked after reading from document.Returns content if operation was successful else returns error message
Params           |  []()       
---------------- | ------- 

```` result | text````

 ____________________________________

> <h3>GotUri</h3>Event invoked when user selects a document or tree from SAF file picker
Params           |  []()       
---------------- | ------- 

```` uri | any````
```` uriString | text````

 ____________________________________

> <h3>GotWriteResult</h3>Event invoked after writing to document.Returns document's uri if operation was successful else returns error message
Params           |  []()       
---------------- | ------- 

```` response | text````

 ____________________________________

> <h2> Methods </h2> 

 > <h3>BuildChildDocumentsUriUsingTree</h3>Builds child documents id using tree (documents which is child of parent document) uri and its parent document id
Params           |  []()       
---------------- | ------- 

```` treeUri | text````<br>
```` parentDocumentId | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>BuildDocumentUriUsingTree</h3>Builds document uri using tree uri and document id
Params           |  []()       
---------------- | ------- 

```` treeUri | text````<br>
```` documentId | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>CopyDocument</h3>Tries to copy document from source uri to target dir
Params           |  []()       
---------------- | ------- 

```` sourceUri | text````<br>
```` targetParentUri | text````<br>

____________________________________

> <h3>CreateDocument</h3>Creates a new and empty document.If document already exists then an incremental value will be suffixed.
Params           |  []()       
---------------- | ------- 

```` parentDocumentUri | text````<br>
```` fileName | text````<br>
```` mimeType | text````<br>

____________________________________

> <h3>CreateFlags</h3>Combines two flags and returns resulting flag
Params           |  []()       
---------------- | ------- 

```` f1 | number````<br>
```` f2 | number````<br>

<i>Return type : number</i>

____________________________________

> <h3>DeleteDocument</h3>Tries to delete document from given uri and returns result
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

<i>Return type : boolean</i>

____________________________________

> <h3>GetDisplayName</h3>Returns display name of given document uri
Params           |  []()       
---------------- | ------- 

```` documentUri | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>GetDocumentId</h3>Returns document id of an uri (should only be grand child)
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>GetLastModifiedTime</h3>Returns last modified time (epoch) of given document uri
Params           |  []()       
---------------- | ------- 

```` documentUri | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>GetMimeType</h3>Returns mime type of given document uri
Params           |  []()       
---------------- | ------- 

```` documentUri | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>GetSize</h3>Returns size (in bytes) of given document uri
Params           |  []()       
---------------- | ------- 

```` documentUri | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>GetTreeDocumentId</h3>Returns document id of tree uri (should be either tree uri itself or a direct child uri)
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>IsChildDocumentUri</h3>Returns whether second uri is child of first uri
Params           |  []()       
---------------- | ------- 

```` parentUri | text````<br>
```` childUri | text````<br>

<i>Return type : boolean</i>

____________________________________

> <h3>IsDocumentUri</h3>Returns whether given uri is a document uri
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

<i>Return type : boolean</i>

____________________________________

> <h3>IsReadGranted</h3>Returns whether read is available for given uri
Params           |  []()       
---------------- | ------- 

```` uri | text````<br>

<i>Return type : boolean</i>

____________________________________

> <h3>IsTreeUri</h3>Returns whether given uri is a tree uri
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

<i>Return type : boolean</i>

____________________________________

> <h3>IsWriteGranted</h3>Returns whether write is available for given uri
Params           |  []()       
---------------- | ------- 

```` uri | text````<br>

<i>Return type : boolean</i>

____________________________________

> <h3>ListFiles</h3>Tries to list files from given dir
Params           |  []()       
---------------- | ------- 

```` dirUri | text````<br>
```` dirDocumentId | text````<br>

____________________________________

> <h3>MoveDocument</h3>Tries to move document from source uri to target dir
Params           |  []()       
---------------- | ------- 

```` sourceUri | text````<br>
```` sourceParentUri | text````<br>
```` targetParentUri | text````<br>

____________________________________

> <h3>OpenDocumentTree</h3>Prompts user to select a document tree
Params           |  []()       
---------------- | ------- 

```` title | text````<br>
```` initialDir | text````<br>

____________________________________

> <h3>OpenSingleDocument</h3>Prompts user to select a single file
Params           |  []()       
---------------- | ------- 

```` title | text````<br>
```` category | text````<br>
```` type | text````<br>
```` extraMimeTypes | list````<br>

____________________________________

> <h3>ReadFromFile</h3>Reads from given uri
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

____________________________________

> <h3>ReleasePermission</h3>Relinquish a persisted URI permission grant
Params           |  []()       
---------------- | ------- 

```` uri | text````<br>
```` flags | number````<br>

____________________________________

> <h3>RenameDocument</h3>Tries to rename a document and returns updated uri
Params           |  []()       
---------------- | ------- 

```` documentUri | text````<br>
```` displayName | text````<br>

<i>Return type : text</i>

____________________________________

> <h3>StringFromUriObject</h3>Convert uri to string
Params           |  []()       
---------------- | ------- 

```` uri | any````<br>

<i>Return type : text</i>

____________________________________

> <h3>StringToUriObject</h3>Converts string to uri
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>

<i>Return type : any</i>

____________________________________

> <h3>TakePersistableUriPermission</h3>Take a persistable URI permission grant that has been offered. Once taken, the permission grant will be remembered across device reboots.
Params           |  []()       
---------------- | ------- 

```` uri | any````<br>
```` flags | number````<br>

____________________________________

> <h3>WriteToFile</h3>Writes to given uri
Params           |  []()       
---------------- | ------- 

```` uriString | text````<br>
```` content | text````<br>

____________________________________

> <h2> Properties </h2> 

 > <h3>DocumentDirMimeType</h3>Returns mime type of document dir
<i>Property Type : read-only</i><br><i>Accepts : text</i>
____________________________________

> <h3>FlagGrantReadPermission</h3>Flag to get write permission
<i>Property Type : read-only</i><br><i>Accepts : number</i>
____________________________________

> <h3>FlagGrantWritePermission</h3>Flag to get read permission
<i>Property Type : read-only</i><br><i>Accepts : number</i>
____________________________________
