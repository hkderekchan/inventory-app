# inventory-app

# Intro

This app supports these features:
1. providing API(s) for inventory maintenance (create, update quantity, list per page & category, delete)
2. providing API(s) for category maintenance (create, list, delete)
3. maintaining categories in parent/child relationship, supporting multi level category tree structure

Assumptions
- total number of categories should be a small value, eg. under 1000
- categories supports multi level structure, but for this first release, we will discuss the two-level setup (ie. a simple category & sub-category setting)
- categories should be properly set up (eg. "Cake" should be created under "Food", and "Shoes" should be created under "Clothes") before inventory records are created
- inventory is created under a sub-category, and automatically inherits the category (sub-category's parent is category) information; so, an inventory record should not have inconsistent information like category being "Food" and sub-category being "Shoes"
- inventory can be navigated page by page (20 records per page, sort by name), with/without sub-category filter
- category/inventory name is restricted to a character set, expressed in regular expression, it is: `[0-9a-zA-Z\- ]+`

These features would help to construct these workflows, which can be demonstrated with the attached Postman collection:
1. admin create category, or sub-category (if a category is selected as parent)
2. admin/user can see what categories are created, in a tree structure
3. if any category is wrongly created, admin can delete it before adding inventory to it
4. admin can create inventory under a sub-category by selecting a category, and then a sub-category under it
5. if the inventory name is wrongly set, admin can delete it at once
6. admin/user can see what inventory are created, without selecting a sub-category; or he can also filter by sub-category
7. admin can update quantity of an inventory

# How to run
1. Git clone the project
2. Import the project to Spring Tools Suite
3. Right-click on the project, and then Run as > Spring Boot App   
The app will then run on localhost, port 8080, ie. you can access the API(s) from http://localhost:8080/api/v1/xxxx   
A set of sample calls are packaged as Postman collection, please import the attached Postman collection to Postman.   
4. For demo purpose, the app will run on an in-memory db, ie. data will be reset after restart
5. During the demo, you can check db data from http://localhost:8080/h2-console , input JDBC URL as `jdbc:h2:mem:mydb`
![image](https://user-images.githubusercontent.com/6152741/134753900-3be44cd3-0183-4bc1-b4c8-7ca77b51b305.png)

# Attachments   
[Postman collection](https://github.com/hkderekchan/inventory-app/blob/main/inventory-app%20demo.postman_collection.json)   
[Sample request/response json files](https://github.com/hkderekchan/inventory-app/blob/main/sample%20jsons.zip)   
