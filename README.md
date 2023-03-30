# ShareIt
Backend service for sharing items.

ShareIt allows you to book item for certain dates, close access to reserved items,
add new item to share on request, if the desired item is not available.

### Functional:

1. User can add new items and will be considered its owner, also can add name, description to this items.
2. You can search for item you need. The item is booked for certain dates. The owner of the item must confirm the booking.
3. If the desired item is not found in the search, the user can create a request. The other users can add desire item.
4. After using the item, users can leave feedback.

### Technology stack:
Java 11, Spring Framework, Docker, PostgreSQL, Maven, Spring Data JPA, Hibernate

### System requirements:
* JDK 11
* Docker

### Startup instructions:
1. Download zip-file  
2. Unpack zip-файл  
3. Open app in IntellijIdea  
4. mvn clean package
5. docker-compose up
6. Test app with postman tests collection in postman package
