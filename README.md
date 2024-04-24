## Searching
You can search using the following parameters:  
Type : CLASS / METHOD / FIELD  
Mappings : INTERMEDIARY / OBFUSCATED <client/server/both> / BABRIC / MCP <client/server/both> / ALL 
Filter : FUZZY / STRICT / SUPERSTRICT  

### Syntax
None of the arguments are required, if you only put in your query it will be executed in a carper-bomb search of all types and mappings with fuzzy filtering   

`[<c | m | f> [i | o/oc/os | b | m/mc/ms] <? | !>] <query>`    

Argument 1 : Type  
c - class  
m - method  
f - field  

Argument 2 : Mappings  
i - intermediary  
o - obfuscated | oc - obfuscated client | os - obfuscated server  
b - babric  
m - mcp | mc - mcp client | ms - mcp server  

Argument 3 : Filter  
? - fuzzy  
! - strict  
!! - superstrict  

### Examples
`c?class_17` - This wil match classes like `class_17`, `class_171`, `class_177`  
`c!class_17` - This will only match `class_17`  
`m!method_16` - This will only match `method_16` and display its parent method  
`m!!method_16`- This will only match `method_16` and display only it, nothing else  
`cm?Block` - This will only search for the Block class name in MCP style mappings  
`cmc?Block` - This will only search for the Block class name in MCP **Client** style mappings  
