# dog-api

AsyncTask was deprecated in API level 30 (Android 11). Here are two examples of how AsyncTask can be replaced.

Using Dog API (https://dog.ceo/dog-api/) we receive a JSON containing a link to a picture, then we get a picture using this link. We have 2 asynchronous operations need to be performed sequentially.

In the first option we use HandlerThread (https://developer.android.com/reference/android/os/HandlerThread) and messages exchange so that requests are executed sequentially.

In the second version we use Java's Executors (https://developer.android.com/reference/java/util/concurrent/Executors). We create an Executor using the newSingleThreadExecutor () method which uses one thread working in an unlimited queue where tasks are guaranteed to be executed sequentially and only one task can be active at any point in time.

---

AsyncTask был объявлен deprecated в 30 API level. (Android 11) Здесь два примера, чем можно заменить AsyncTask.

На примере Dog API (https://dog.ceo/dog-api/), получаем JSON содержащий ссылку на картинку, затем получаем по этой сслыке картинку. Имеем 2 асинхронные операции, которые нужно выполнить последовательно.

В первом варианте используем HandlerThread (https://developer.android.com/reference/android/os/HandlerThread) и обмен сообщениями, чтобы запросы выполнились последовательно.

Во втором варианте используем Java-овский Executors (https://developer.android.com/reference/java/util/concurrent/Executors). Методом newSingleThreadExecutor() создаем Executor, который использует один рабочий поток, работающий в неограниченной очереди, где задачи гарантированно выполняются последовательно, и не более одной задачи может быть активной в любой момент времени.
