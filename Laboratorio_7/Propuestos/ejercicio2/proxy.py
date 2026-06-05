from flask import Flask, request, jsonify
from zeep import Client

app = Flask(__name__)
WSDL = 'http://www.dneonline.com/calculator.asmx?WSDL'


@app.route('/proxy', methods=['POST'])
def proxy():
  data = request.get_json(force=True)
  action = data.get('action', 'Add')
  a = data.get('a')
  b = data.get('b')
  if a is None or b is None:
    return jsonify({'error': 'provide a and b'}), 400

  client = Client(WSDL)
  # support basic calculator actions exposed by the public WSDL
  if action in ('Add', 'Subtract', 'Multiply', 'Divide'):
    try:
      func = getattr(client.service, action)
      result = func(int(a), int(b))
      return jsonify({'result': result})
    except Exception as e:
      return jsonify({'error': str(e)}), 500
  else:
    return jsonify({'error': 'unsupported action'}), 400


if __name__ == '__main__':
  app.run(host='127.0.0.1', port=5000, debug=True)
