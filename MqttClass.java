
     //replace these with appropriate broker content 
    public static final String BROKER="tcp://HOST_URL:PORT";
    public static final String USERNAME="";
    public static final String PASSWORD="";
    
    String topic="home/sensor";
    MqttAndroidClient client;
    MqttConnectOptions options;
    //publish button
    Button send;
    //display subscribe content if any
    TextView text;
    
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_test);
        send=(Button)findViewById(R.id.send_id);
        text=(TextView)findViewById(R.id.sub);
        
        MqttSetup();
        MqttConnect();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publish(topic,"hello world");
            }
        });

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }
            
            //background notification 
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                        text.setText("Topic :"+topic+"\nMessage :"+message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }
    void MqttSetup(){
        
        //client settings and communication options
        String clientId = "ExampleAndroidClient";
        client = new MqttAndroidClient(MqttTest.this,BROKER,clientId);
        options = new MqttConnectOptions();
        //options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
    }
    void MqttConnect(){
        try {
            final IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    MainActivity.myToast("connected,token :"+asyncActionToken.toString(),MqttTest.this);
                    subscribe(topic, (byte) 0);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    MainActivity.myToast("not connected",MqttTest.this);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    void publish(String topic,String msg){

        //0 is the Qos
        options.setWill(topic, msg.getBytes() ,0,false);
        try{
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    MainActivity.myToast("SEND DONE,token :"+asyncActionToken.toString(),MqttTest.this);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    MainActivity.myToast("publish error"+asyncActionToken.toString(),MqttTest.this);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    void subscribe(String topic,byte qos){

        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    MainActivity.myToast("suscribed "+asyncActionToken.toString(),MqttTest.this);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                  
                    MainActivity.myToast("subscribing Error",MqttTest.this);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    void unsubscribe(String topic){

        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    MainActivity.myToast("Unsubscribed",MqttTest.this);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                  Throwable exception) {  
                    MainActivity.myToast("couldn't unregister",MqttTest.this);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    void disconnect(){
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                   MainActivity.myToast("disconnected",MqttTest.this);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    MainActivity.myToast("couldn't disconnect",MqttTest.this);
                                                          }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
