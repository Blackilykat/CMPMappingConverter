# CMPMappingConverter
A program that takes classic MCP mappings and converts them to work with RetroMCP.

This was originally written for Respouted and has only been tested with 1.6.4 (mcp 8.11) so far.

## What's missing?
Currently, generating the `exceptions.exc` file and getting type of fields hasn't been implemented yet.

## How do I use this?
Add the following files found in mcp's `conf` directory to this program's working directory:
 - client.srg
 - fields.csv
 - joined.exc
 - methods.csv
 - server.srg

Then, run the program and it should create a file called `mappings.tiny` in the same directory containing the converted mappings.
