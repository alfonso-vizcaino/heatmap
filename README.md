# heatmap
Build a heatmap with OpenAQ API service

# Run app
To Run application 
1) git clone repository
2) go to folder 
3) run 
   mvn spring-boot:run
# Main API Documentation
To view the api documentation lunch the application following previus commands and then go to these links <br>
http://localhost:8080/swagger-ui/ <br>
http://localhost:8080/v2/api-docs <br>

# Sample Endpoints
http://localhost:8080/v1/heatmap/countries <br>
http://localhost:8080/v1/heatmap/countries/US/pm25 <br>
http://localhost:8080/v1/heatmap/location?latitude=33.92907&longitude=-118.2309&radius=80000&param=pm25 <br>