package connector;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Scanner;


public class YooMoneyConnector {

    private String client_id;
    private String code;
    private String receiver;

    private String redirect_uri;
    private String access_token;

    private OkHttpClient client;
    private RequestBody formBody;
    private Request request;
    private Response response;

    private Proxy proxy;

    public YooMoneyConnector(Proxy proxy, String client_id, String redirect_uri, String receiver) {
        this.redirect_uri = redirect_uri;
        this.receiver = receiver;
        this.client_id = client_id;
        client = new OkHttpClient.Builder().proxy(proxy).build();
    }

    public YooMoneyConnector(String client_id, String redirect_uri, String receiver) {
        this.client_id = client_id;
        this.redirect_uri = redirect_uri;
        this.receiver = receiver;
        client = new OkHttpClient.Builder().build();
    }

    public void auth() {
        formBody = new FormBody.Builder()
                .add("client_id", client_id)
                .add("response_type", "code")
                .add("redirect_uri", redirect_uri)
                .add("scope", "account-info operation-history operation-details incoming-transfers")
                .build();
        try {
            int request_length = (int) formBody.contentLength();
            request = new Request.Builder()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Content-Length", String.valueOf(request_length)) //Без этого параметра  - ошибка, invalid_request
                    .url(ConstantsHosts.HOST_FOR_AUTH)
                    .post(formBody)
                    .build();
            response = client.newCall(request).execute();
            if (response.message().equals("OK") && response.toString().contains("code=200")) {
                String uri = response.toString();
                String url = uri.substring(uri.indexOf("url=") + 4, uri.length() - 1);
                System.out.println("Посетите веб сайт и подтвердите разрешение приложению. Ссылка: " + url);

            } else {
                System.out.println("Ошибка при запросе. Проверьте вводимые client_id  и redirect_uri");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void changeToken() throws IOException {
        save_code_from_console();
        formBody = new FormBody.Builder()
                .add("code", code)
                .add("client_id", client_id)
                .add("grant_type", "authorization_code")
                .add("redirect_uri", redirect_uri)
                .build();

        request = new Request.Builder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Content-Length", String.valueOf(formBody.contentLength()))
                .url(ConstantsHosts.HOST_FOR_CHANGE_TOKEN)
                .post(formBody)
                .build();
        HashMap<String, String> data_from_request = getMapFromResponse(request);
        access_token = data_from_request.get("access_token");
        System.out.println("Ваш токен : " + access_token + "\nСохраните его, вы будете использовать его для выставления счёта.");
    }


    public HashMap<String, String> getAccountInfo() throws IOException {
        formBody = new FormBody.Builder()
                .build();
        request = new Request.Builder()
                .header("Authorization", "Bearer " + access_token)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .url(ConstantsHosts.HOST_FOR_ACCOUNT_INFO)
                .post(formBody)
                .build();
        return getMapFromResponse(request);
    }

    public HashMap<String, String> getAccountHistory(String records_count) throws IOException {
        formBody = new FormBody.Builder()
                .add("code", records_count)
                .build();
        request = new Request.Builder()
                .header("Authorization", "Bearer " + access_token)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .url(ConstantsHosts.HOST_FOR_ACCOUNT_HISTORY)
                .post(formBody)
                .build();
        return getMapFromResponse(request);
    }

    public void sendpayment(String receiver, String form_name, String target, String paymentType_1, String paymentType_2, String sum, String label) throws IOException {

        formBody = new FormBody.Builder()
                .add("receiver", receiver)
                .add("quickpay-form", form_name)
                .add("targets", target)
                .add("paymentType", paymentType_1)
                .add("paymentType", paymentType_2)
                .add("sum", sum)
                .add("label", label)
                .build();
        request = new Request.Builder()
                .header("Authorization", "Bearer " + access_token)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Content-Length", String.valueOf(formBody.contentLength()))
                .url(ConstantsHosts.HOST_FOR_SEND_PAYMENT)
                .post(formBody)
                .build();

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String uri = response.toString();
        String url = uri.substring(uri.indexOf("url=") + 4, uri.length() - 1);
        System.out.println(url);
    }

    private void save_code_from_console() {
        System.out.println("После успешного подтверждения, вас переправит по вашей redirect_uri, вместе с кодом. Пример https://your_redirect_uri?code=XXXXXXXXXXXXX" +
                "\nПожалуйста, введите код, который идёт после слова code : ");
        Scanner sc = new Scanner(System.in);
        code = sc.nextLine();
        System.out.println("Это ваш код : " + code);
    }

    private HashMap<String, String> getMapFromResponse(Request request) throws IOException {
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String uri = response.body().string();
        JSONObject json = new JSONObject(uri);
        return (HashMap) json.toMap();
    }





}