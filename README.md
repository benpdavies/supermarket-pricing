# supermarket-pricing

This repository contains a Clojure based model for a supermarket pricing system. The aim is to represent a shopping basket and receipt in a way that allows for different pricing structures like:

* Three tins of beans for the price of two
* Onions for 29p / kg
* Two cans of coca-cola for £1
* Any 3 ales from the set {...} for £6

## Process

I started by considering the most basic system where the model can find the basket's total by summing the quantities of each item found in the basket times by the price of that item found in a prices database. The pricing system was steadily built up by including specific units for each item and the price per unit.

In order to include pricing offers, like what a supermarket might offer, the individual offer types were added in turn. The model is set up so that each offer has a different name, item and parameter values. To create a new offer the user just has to then write a new function to calculate the saving associated with that offer. Each offer follows the same format:

1. Find the appropriate items for that offer
2. Calculate the original price (as if the offer didn't exist)
3. Calculate the reduced price including the :offer-type offer
4. Find the difference

## Usage

To use the model a basket must be defined as a map:
```python
(def basket {:beans 3 :coke 3 :onions 0.2 :ale1 1 :ale2 2 :ale3 2 :ale4 1})
```

Then any offers the model uses can be defined using the offer functions:
```python
(def my-offer (offer-fixed-price "coke" 2 1))
```

To form the receipt use `form-receipt` with the basket and any offers:
```python
(def receipt (form-receipt basket my-offer))
```

To view the processed receipt with associated prices and savings:
```python
(def process-receipt receipt)
```

## Limitations and Improvements

FIXME: listing of options this app accepts.

## Examples

...
