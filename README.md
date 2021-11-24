# YooMoneyConnector
Коннектор для юмани.

Приветствую. Не думаю, что данный туториал понадобится на англ. языке, поэтому сделаю на русском, дальше посмотрим. Код проекта писал в быстром темпе, так что конечно, всё далеко не оптимизировано, но работает.

По сути, это обёртка над апи юмани, для удобной аутентификации, и получении информации о кошельке, чтобы можно было выставлять счета и получать платежи на кошелёк.


Итак, если вы хотите получать платежи на кошелёк юмани из вашего приложения, вам нужно:
1. Зарегистрироваться на юмани.
2. Дальше зарегистрировать своё приложение [здесь](https://yoomoney.ru/myservices/new?from=auth&yooid-auth-success=true). Сохраняем значение redirect_uri, которое вы введёте, также сохраянем номер вашего кошелька (делее receiver). Получаем client_id, это тоже сохраняем.


Если будете использовать мой код без изменений в своих проектах, далее вы:

1. Создаёте класс:

`YooMoneyConnector yooMoneyConnector = new YooMoneyConnector("YOUR_CLIENT_ID", "YOUR_REDIRECT_URI", "RECEIVER");`

Если нужно proxy, то есть дополнительный конструктор 

`YooMoneyConnector yooMoneyConnector = new YooMoneyConnector(proxy, "YOUR_CLIENT_ID", "YOUR_REDIRECT_URI", "RECEIVER");`

Параметр proxy принимает на вход прокси, Например: 

 `InetSocketAddress proxyAddress = new InetSocketAddress("your_host", port);`
 
  `Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);`
        
2. Далее выполняем метод, который будет ожидать от вас ввода кода:

`yooMoneyConnector.auth();`

Он отправляет запрос на хостинг, для получения краткосрочного токена, который нам нужно будет обменять на постоянный. Также этот метод передаёт список параметров, которые дают разрешения нашему приложению. В данном случае, мне требовалась лишь информация о платежах и аккаунтах, как вы видите в коде.

Если всё хорошо, в консоли вы увидете сообщение
Посетите веб сайт и подтвердите разрешение приложению. Ссылка: " + url,

где url - ссылка, по которой вам нужно будет пройти в браузере, и подтвердить в настройках своего аккаунта разрешение данному приложению иметь доступ. После того, как вы сделаете это, вас переправит на ваш redirect_uri добавив к нему параметр code, который будет равен единовременному токену. (https://your_redirec_uri?code=XXXXXXXXXXXXXXXXXXXXXXXXX). 
Скопируйте значение code и вставьте в консоль.

После этого, не раздумывая долго, смело вызывайте метод 

`yooMoneyConnector.changeToken();`

Если всё ОК, то после работы метода, вы увидите в консоли свой токен. Сохраняем его! Теперь приложение и юмани связаны, прекрасно!

Данный метод возвращает HashMap с данными вашего аккаунта.

`yooMoneyConnector.getAccountInfo();`

Теперь перейдём к выставлению счёта. 

Вызываем метод 

`yooMoneyConnector.sendpayment("your_receiver", тип платежа (например "donate"), описание платеж (Например "Платёж от моего приложения"), (Параметры для оплаты через юмани)"PC",(Параметр для оплаты банковской картой) "AC",(Сумма платежа) "10",(САМОЕ главное - label платежа, по нему мы будем определять того, кто оплатил) "id_покупателя_001");`

В ответ вызов этого метода, мы получем url - счета, который нужно отправить покупателю. 

Далее проверяем наш аккаунт, методом 

`yooMoneyConnector.getAccountHistory((Количество записей, которые мы получим)"3");`

В ответ получаем Объект Operations с данными истории. Проверяем этот объект на значение id_покупателя_001, если там такое значение есть, проверяем у этого значение поле status, если оно равняется success, значит всё ок, деньги у нас на кошельке.

Вроде всё, если что пишите.



