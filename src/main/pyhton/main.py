import math


def is_square(num):
    return int(math.sqrt(num)) == math.sqrt(num)


def print_primes(num):
    if num < 2:
        return
    for i in range(2, num):
        accept = True
        for j in range(2, i):
            if i % j == 0:
                accept = False
                break
        if accept:
            print(i)


print(print_primes(100))

print(math.sqrt(12))
