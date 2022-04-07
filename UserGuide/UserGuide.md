# API User guide

This document describes how to use each REST API endpoint exposed by this server.\
To create libraries or get a list of existing libraries look into [Libraries](#Libraries).\
To interact with a specific library, by creating, retrieving or modifying entries, look into [Library](#Library).

To:
 - Create a new study
 - Get a list of existing studies
 - Start an automated search for a given study
 - Retrieve or interact with the results of a study
 
look into [Studies](#Studies).

## Libraries

### **Path:** /libraries


- **GET:** Provides a list of existing libraries within the working directory.
  - Example response:
      ``` json
      {
          "libraryNames" : [ "arxiv.bib", "ieeexplore.bib" ]
      }
      ```
- **POST:** Create a new library with the given name, provided in the request body, note that names have to be unique, 
so names returned by the GET method above are not allowed and will result in an `HTTP 409 CONFLICT`. 
    
  - Expected request body for creating a library with the name `newLib`: 
    ``` json 
    {
        "libraryName" : "newLib"
    }
    ```
  
##Library

### **Path:** /libraries/{libraryName}


- **GET:** Returns all entries contained in the library with the provided name. Note that the bib file extension can be **omitted**.\
Note that library names are case-sensitive!
  - Example response (note that there are many more possible fields such as `abstract`):
    ``` json 
    {
      "bibEntries" : [ {
          "entrytype" : "Article",
          "citekey" : "Saha2018",
          "author" : "Prashanta Saha and Upulee Kanewala",
          "date" : "2018-02-20",
          "keywords" : "cs.SE",
          "title" : "Fault Detection Effectiveness of Source Test Case Generation Strategies"
      }, {
          "entrytype" : "Article",
          "citekey" : "Zhu2019",
          "author" : "Hong Zhu and Ian Bayley and Dongmei Liu and Xiaoyu Zheng",
          "date" : "2019-12-20",
          "keywords" : "cs.SE",
          "title" : "Morphy: A Datamorphic Software Test Automation Tool"
      } ]
    }
    ```
    
- **POST:** Adds a new entry provided in the request body to the library. 
Note that the citation key in the provided must be non-blank and unique within the library. 
Note that citation keys are case-sensitive.
  - Example request body:
    ``` json 
    {
      "entry": {
          "entrytype":"Article",
          "citekey":"Saha2017",
          "author":"Prashanta Saha and Upulee Kanewala",
          "date":"2018-02-20",
          "keywords":"cs.SE",
          "title":"Fault Detection Effectiveness of Source Test Case Generation Strategies"
      }
    }
    ```
- **DELETE:** Delete the library with the provided name.

### **Path:** /libraries/{libraryName}/{citeKey}

- **GET:** Returns the entry with the provided citation key from in the library with the provided name. Note that citation keys are case-sensitive.
  - Example response:
    ``` json 
    {
      "entry" : {
          "entrytype" : "Article",
          "citekey" : "Saha2018",
          "author" : "Prashanta Saha and Upulee Kanewala",
          "date" : "2018-02-20",
          "keywords" : "cs.SE",
          "title" : "Fault Detection Effectiveness of Source Test Case Generation Strategies"
      }
    }
    ```

- **PUT:** Updates the existing entry with the provided citation key with the entry provided in the request body. 
Note that the citation key in the provided must be non-blank and unique within the library.
Note that citation keys are case-sensitive.
  - Example request body:
    ``` json 
    {
      "entry": {
          "entrytype":"Article",
          "citekey":"Saha2017",
          "author":"Prashanta Saha and Upulee Kanewala",
          "date":"2018-02-20",
          "keywords":"cs.SE",
          "title":"Fault Detection Effectiveness of Source Test Case Generation Strategies"
      }
    }
    ```
- **DELETE:** Delete the entry with the provided citation key.

## Studies

### **Path:** /studies

- **GET:** Returns a list of all studies within the studies' directory in the work-directory.
  - Example response:
    ``` json 
    {
      "studyNames" : [ "Study1", "Study2" ]
    }
    ```

- **POST:** Creates a new study defined by the study definition provided in the request body.
  - Example request body:4
    ``` json 
    {
      "studyDefinition": {
        "authors": [
          "TestAuthor1",
          "TestAuthor2"
        ],
        "title": "Test2",
        "research-questions": [
          "TestQ1"
        ],
        "queries": [
          {
            "query": "test"
          },
          {
            "query": "test-driven"
          }
        ],
        "databases": [
          {
            "name": "ArXiv",
            "enabled": true
          },
          {
            "name": "IEEEXplore",
            "enabled": false
          }
        ]
      }
    }
    ```
### **Path:** /studies/{studyName}
- **Delete:** Deletes the study and the corresponding directory.

### **Path:** /studies/{studyName}/results
- This endpoint corresponds to the library endpoint with the libraryName being results, the same operations as there can be used.

### **Path:** /studies/{studyName}/crawl
- **GET:** Returns whether a crawl is currently running for the specified study.
  - Example response:
    ```json
    {
      "currentlyCrawling" : false
    }
    ```
- **POST:** Start a crawl session for the specified study. If there is already a crawl running, this operation does nothing.
