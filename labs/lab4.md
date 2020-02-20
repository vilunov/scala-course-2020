# Lab 4 – Streaming Processing

Author: [@evgerher](https://t.me/evgerher)

## Subject: Metrics for HFT

In this homework you have to implement several simple metrics widely used in High Frequency Trading.

## Prerequisites:
<div style="text-align: center">
  <img src="https://i.imgur.com/vl5MnWO.png">
  <p>
    Image 1. Order book visualization.<br/>
    Source: Bitmex cryptoexchange.
  </p>
  <img src="https://i.imgur.com/zcDg3oZ.png">
  <p>
    Image 2. Depth chart.<br/>
    Source: Bitmex cryptoexchange.
  </p>
</div>

**Orderbook** ("Стакан") keeps tracks of most relevant trades - closest to best price for bid and ask sides. It differentiates by depth and information provided:

- L1: only best bid and ask available
- L2: up to $k$ best prices and volumes available for both sides (typically k = 25).
- L3: L2 with differentiation of each price (orders of specific size and placement in a queue).

**During this task we'll be using $L2$ dataset.**

On a **Depth chart** you may see a highly observable orderbook imbalance, very soon in 2-3 ticks price will grow by 1-2 price steps as there is not enough bids (red part).



## Instant metrics
- Midpoint - a midpoint between best bid and ask prices.
$midpoint = \frac{(bid_{best} + ask_{best})}{2}$
- VWAP (Volume Weighted Average Price) - allows to smooth importance of small  orders.
1. $\frac{\sum_1^N{v_i*a_i}}{\sum_1^N{v_j}} | \sum_1^N v_i \le c$ - VWAP for ask
2. $\frac{\sum_1^N{v_i*b_i}}{\sum_1^N{v_j}} | \sum_1^N v_i \le c$ - VWAP for bid 
3. $c = \text{1 million}$ by default – make it an HTTP get parameter
Where N defines the size of the orderbook.

- VWAP midpoint - a midpoint between VWAPs bid and ask prices.

$VWAP_{midpoint} = \frac{VWAP_{bid} + VWAP_{ask}}{2}$

## Continuous metrics
- $SMA_t$ - simple moving average which averages last $n$ observations
$SMA_t = \frac{\sum^T_{T-t}{a_i}}{t}$
- $EMA_n$ - exponential moving average which keeps more focus on latest observations.
$EMA_t = \alpha*p_t + (1-\alpha) EMA_{t-1}$, $\alpha \in (0, 1); \alpha = \frac{2}{n+1}$


## Dataset

Provided dataset consists of 5000 L2 orderbook snapshots from Bitmex exchange. The data can be found in the example project repository.

_Structure_: each row consists of 102 fields

- timestamp
- symbol (XBTUSD or ETHUSD)
- 25 pairs (price: Float32, volume: UInt32) :: asks
- 25 pairs (price: Float32, volume: UInt32) :: bids

## Task

1. Implement EMA using streams.
2. Provide access to instant metrics via an HTTP endpoint.
3. Provide access to continuous metrics via a parametrized HTTP endpoint.

Example project can be found on [GitHub](https://github.com/vilunov/scala-course-2020/tree/master/labs/lab4-example).