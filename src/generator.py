import argparse
from random import choice 


parser = argparse.ArgumentParser('generate test data')
parser.add_argument('target')
parser.add_argument('level', type=int)
parser.add_argument('baskets', type=int)

def gen_file(target, support_level, number_of_baskets):

    with open(target, 'w') as t:
        t.write(str(support_level))
        t.write('\n')
        for basket in range(number_of_baskets):
             t.write(str(basket))
             size = choice([3,4,5,6,7,8,9,10])
             for _ in range(size):
                 el = choice([0,1,2,3,4,5,6,7,8,9])
                 t.write(',')
                 t.write(str(el))
             t.write('\t')

if __name__ == '__main__':
    args = parser.parse_args()
    target = args.target
    level = args.level
    baskets = args.baskets
    gen_file(target, level, baskets)
