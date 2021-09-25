# inventory-app

Intro

This app supports these features:
1. providing API(s) for inventory (create, update quantity, list per page & category, delete) & category maintenance (create, list, delete)
2. maintaining categories in parent/child relationship, supporting multi level category tree structure

Assumptions
- total number of categories should be a small value, eg. under 1000
- categories supports multi level structure, but for this first release, we will first describe the two-level setup (ie. a simple category & sub-category setting)
- categories should be properly set up (eg. Cake should be created under Food, and Shoes should be created under Clothes) before inventory records are created
- inventory is created under a sub-category, and automatically inherits the category (sub-category's parent is category) information; so, an inventory record should not have inconsistent information like category being "Food" and sub-category being "Shoe"

These features would help to construct these workflows, and can be demo from the below Postman requests:
1. admin create category or sub-category (if a category is selected as parent)
2. admin can see what categories are created, in a tree structure
3. if any category is wrongly created, admin can delete it before adding inventory to it
4. admin can create inventory under a sub-category by selecting a category, and then a sub-category under it
5. if the inventory name is wrongly set, admin can delete it at once
6. admin can see what inventory are created, without selecting a sub-category; or he can also filter it by selecting a sub-category
7. admin can update quantity of an inventory

How to run
1. Git clone the project
2. Import the project to Spring Tools Suite / Eclipse
3. Right-click on the project, and then Run as > Spring Boot App

The app will then run on localhost, port 8080, ie. you can access the API(s) from http://localhost:8080/api/v1/xxxx 
A set of sample calls are packaged as Postman collection, please import the attach file.
