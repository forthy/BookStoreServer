@echo off

if %1.==. (
	echo No parameters have been provided. You need a json file as parameter
	echo Ex:  %0 jsonfile.json
) else (
	echo "Run post json file to http://127.0.0.1:8888/bookstore/add"
	curl.exe -H "Content-Type: application/json" -v -X POST -d @%1 http://127.0.0.1:8888/bookstore/add
)

@echo on