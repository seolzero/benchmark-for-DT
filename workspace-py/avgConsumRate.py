import json

with open('/home/cotlab/benchmark/output.json', 'r') as file:
    data = json.load(file)

consume_rates = data.get('consumeRate', [])
publishRates = data.get('publishRate', [])
print('consumeRate: {}'.format(consume_rates))
if consume_rates :
    average_consume_rate = sum(consume_rates) / len(consume_rates)
    average_publishRate = sum(publishRates) / len(publishRates)
else:
    average_consume_rate = 0
    average_publishRate = 0

print('Average consumeRate: {}'.format(average_consume_rate))
print('Average publishRate: {}'.format(average_publishRate))
