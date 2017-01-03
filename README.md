# Video Diary
Based on this article https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html I have made app for demonstrate how to use MVP pattern with RX JAVA+Dagger2 and domain driven model. In shame below we divide our app for 3 layer- **Presenter**,**Domain** and **Data** layer. In **Presenter** layer we show information to user.(In this layer we avoid any heavy work. Just present user information from **Data** layer). **Domain** layer it's a simple pure java objects for transform data from presenter to data layer. In **Data** layer we handle all handle process(in our case working with db).

All this layers communicate through interface for being max independent for other. For handle heavy process in **Data** layer we use RXJava technology. Results we send in subscriber in **Presenter** layer and show it to user.

![ddd](https://cloud.githubusercontent.com/assets/2522157/21602051/c616ba98-d198-11e6-8307-4f13c3b02782.jpg)

In Video Dairy we use simple dagger graph for dependency injection. Basically we have 3 scopes @Singltone, @PerActivity, @PerFragment.In diagram below you can see a more about objects lifecicle in video dairy application.



Let's take use case that user want to load list of saved notes. In video



