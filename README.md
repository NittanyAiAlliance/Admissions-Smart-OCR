# Smart OCR Admissions
Smart OCR Admissions is a tool to assist potential students self-report grades from their high school transcript.
## Project Organization
#### UI
The UI is the web interface for both applicants and administrators to use the functionality of Smart OCR Admissions.
#### Java API
The Java API is an HTTPS server which serves as the data backend for the application. The Java API abstracts away database connections, interactions, and manipulation and requests to the OCR module.
#### OCR API
The OCR API performs optical character recognition and tagging using Amazon Textract and named entity-recognition. The OCR module looks for class records on an uploaded high school transcript and attempts to tag course names, earned grades, and credit weights.

## Set Up
#### UI
The UI is run via an npm script. To start the server from the UI directory, use console command: ```npm install``` and ```npm run dev```
#### Java API
Build the Java application to a jar. The application can then be run with ```java -jar api.jar```. This repo does not contain the production database properties file, which is necessary for the running with live data.
