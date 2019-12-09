# json_server_project

- To run, start the server.jar located inside of the /bin directory of this folder.

- While the application is running, you may use a REST client to POST JSON data to
check all of functionality requirements of the server.

- The source can be found in the /src directory.

- Relevant code
	ServerProgram.java (the main service that handles requests)
	LongestCommonSubstring.java (the code to calculate the LCS of the requested strings)

- Notes
	I tried both imports of org.json and JSON.simple to parse the JSON objects but I struggled to get
either library to work. Since this was a core utility of the project, I had to find an alternative
way to read the JSON strings to at least allow me to include the functionality requirements. Because of this,
the reading and writing of proper JSON is suspected to be quite fragile. While it works in most of cases
that I've tested, I believe there will be issues that arise when testing obscure cases of JSON.

Below are a few tests that were designed to test the server
using an Insomnia REST client connecting to http://127.0.0.1/lcs.

~~~ Correct Format Test 1 ~~~

Input:
{
  "setOfStrings" : [{"value" : "comcast"}, 
		    {"value" : "comcastic"}, 
		    {"value" : "broadcaster"}]
}

Expected Response:

HTTP/1.1 200 OK

{
  "lcs": [
    {
      "value": "cast"
    }
  ]
}




~~~ Correct Format Test 2 ~~~

Input:
{
	"setOfStrings": [
		{
			"value": "165c3d5fdea1ccc67d0bfa7a4681d160"
		},
		{
			"value": "bbf73cbf578760d9c4342d518a3ffa94"
		},
		{
			"value": "9448e818ee7fa95978df39ce0a1a9fdf"
		}
	]
}

Expected Response:

HTTP/1.1 200 OK

{
  "lcs": [
    {
      "value": "fa"
    }
  ]
}





~~~ Incorrect Format ~~~

Input:
{
	"setOfStrings": [
		{
			"value": "comcast"
		},
		{
			"value": "comcastic"
		},
		{
			"value": "broadcas
}

Expected Response:

HTTP/1.1 400 Bad Request

The format of the request could not be understood. This could indicate that the supplied format is incorrect. Please try again.





~~~ Empty "setOfStrings" ~~~

Input:
{
	"setOfStrings": []
}

Expected Response:

HTTP/1.1 400 Bad Request

It appears that there are no formatted strings inside the "setOfStrings".





~~~ Duplicate inside "setOfStrings" ~~~

Input:
{
	"setOfStrings": [
		{
			"value": "comcast"
		},
		{
			"value": "comcastic"
		},
		{
			"value": "comcastic"
		},
		{
			"value": "broadcaster"
		}
	]
}

Expected Response:

HTTP/1.1 400 Bad Request

It would appear that there is a duplicate inside the "setOfStrings". The "setOfStrings" must be a set where all entries are unique.





~~~ Multiple longest common substrings ~~~

Input:
{
	"setOfStrings": [
		{
			"value": "jazzflow_comcast"
		},
		{
			"value": "comcastic_flow_jazz"
		},
		{
			"value": "comcastic_jazz_flow"
		},
		{
			"value": "jazzy_broadcaster_that_flows"
		}
	]
}

Expected Response:

HTTP/1.1 200 OK

{
  "lcs": [
    {
      "value": "cast"
    },
    {
      "value": "flow"
    },
    {
      "value": "jazz"
    }
  ]
}