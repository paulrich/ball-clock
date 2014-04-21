#!/usr/bin/env ruby

class BallClock
  
  @@memoized = {}

  def initialize(balls)
    @ones = []
    @fives = []
    @hours = []
    @queue = (0...balls).to_a
  end

  def queue
    Array.new(@queue)
  end

  def transform_queue!(transform)
    @queue = @queue.map { |x| transform[x] }
  end

  def original_arrangement?
    @queue.each_cons(2).all? { |x, y| x < y }
  end

  def self.unique_days(balls)
    if @@memoized[balls]
      return @@memoized[balls]
    end
    ball_clock = BallClock.new(balls)
    ball_clock.cycle!
    cycles = 1
    next_queue = ball_clock.queue
    transform = Hash[(0...balls).zip(next_queue)]
    until ball_clock.original_arrangement?
      ball_clock.transform_queue! transform
      cycles += 1
    end
    @@memoized[balls] = cycles / 2
  end

  def increment!
    @ones.push @queue.shift
    if @ones.size == 5
      @fives.push @ones.pop
      @queue.concat @ones.reverse!
      @ones.clear
    end
    if @fives.size == 12
      @hours.push @fives.pop
      @queue.concat @fives.reverse!
      @fives.clear
    end
    if @hours.size == 12
      last_ball = @hours.pop
      @queue.concat @hours.reverse!
      @hours.clear
      @queue.push last_ball
    end
    self
  end

  def cycle!
    (12 * 60).times {
      increment!
    }
    self
  end

end

if __FILE__ == $0

  mutex = Mutex.new
  queue = []
  output = []

  reader = Thread.new {
    input = true
    while input
      mutex.synchronize {
        unless queue.empty?
          input = queue.shift
          if input
            raise "Value out of range: 27 <= number-of-balls <= 127" if input < 27 || input > 127
            output << {balls: input, days: BallClock::unique_days(input)}
          end
        end
      }
    end
  }  
  
  while(gets)
    next_integer = $_.chomp.to_i
    break if next_integer == 0
    mutex.synchronize {
      queue << next_integer
    }
  end

  mutex.synchronize {
    queue << nil
  }

  reader.join
  output.map { |results|
    puts "#{results[:balls]} balls cycles after #{results[:days]} days."
  }

end
