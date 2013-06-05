import sys, json, xmpp
SERVER = ('gcm.googleapis.com', 5235)
USERNAME = 'ericrgon-garage'
PASSWORD = 'AIzaSyAQdvG8jyi_MII1tbAjkyPQ_KKB5QVPOjE'

# Unique message id for downstream messages
sent_message_id = 0

def message_callback(session, message):
  global sent_message_id
  gcm = message.getTags('gcm')

  if gcm:
    gcm_json = gcm[0].getData()
    msg = json.loads(gcm_json)
    msg_id = msg['message_id']
    device_reg_id = msg['from']

    # Ignore non-standard messages (e.g. acks/nacks).
    if not msg.has_key('message_type'):
      # Acknowledge the incoming message.
      send({'to': device_reg_id,
            'message_type': 'ack',
            'message_id': msg_id})

      # Send a response back to the server.
      send({'to': device_reg_id,
            'message_id' : str(sent_message_id),
            'data': {'pong': 1}})
      sent_message_id = sent_message_id + 1

def send(json_dict):
  template = ("<message from='{0}' to='gcm@google.com'>"
              "<gcm xmlns='google:mobile:data'>{1}</gcm></message>")
  client.send(xmpp.protocol.Message(
      node=template.format(client.Bind.bound[0],
                           json.dumps(json_dict))))

client = xmpp.Client(SERVER[0], debug=['socket'])
client.connect(server=SERVER, secure=1, use_srv=False)
auth = client.auth(USERNAME, PASSWORD, 'test')
if not auth:
  print 'Authentication failed!'
  sys.exit(1)

client.RegisterHandler('message', message_callback)

while True:
  client.Process(1)