# Video Diary
Based on this article https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html I have made app for demonstrate how to use MVP pattern with RX JAVA+Dagger2 and layer patterns in android app. In shame below we divide our app for 3 layer- **Presentation**,**Domain** and **Data** layer. In **Presentation** layer we show information to user.(In this layer we avoid any heavy work. Just present user information from **Data** layer). **Domain** layer it's a simple pure java objects for transform data from presenter to data layer. In **Data** layer we handle all handle process(in our case working with db).

All this layers communicate through interface or abstract classes for being max independent for other. For handle heavy process in **Data** layer we use RXJava technology. Results we send in subscriber in **Presentation** layer and show it to user.

![ddd](https://cloud.githubusercontent.com/assets/2522157/21602051/c616ba98-d198-11e6-8307-4f13c3b02782.jpg)

In Video Dairy we use simple dagger graph for dependency injection. Basically we have 3 scopes @Singltone, @PerActivity, @PerFragment.In diagram below you can see a more about objects lifecicle in video dairy application.

![dagge2](https://cloud.githubusercontent.com/assets/2522157/21602575/fde3761e-d19d-11e6-88f6-b2eaeefff877.jpg)

**Presentation** layer work through MVP patter. For View we understand Fragments,Activitiews and CustomViews. Presenter it's object for communicate with model in our case Model it's **Domain** layer. Then **Domain** layer call **Data** layer for featch information from data base and back it to Presenter and show it to view. 
![mvp](https://cloud.githubusercontent.com/assets/2522157/21604105/003c339a-d1aa-11e6-9173-d5f4567e25b4.jpg)

In the diagram below there is general schema how app is works. We will take as example load saved notes.
![all app shema](https://cloud.githubusercontent.com/assets/2522157/21603618/52236384-d1a7-11e6-8057-b43b4235d211.jpg)

When you will follow this patterns your code will be more clear and easy for understanding!


