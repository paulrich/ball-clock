#!/usr/bin/env ruby

class BallClock
  
  @@memoized = {}

  def initialize (balls)
    raise "Number of balls must be between 27 and 127" if balls < 27 || balls > 127
    @ones = []
    @fives = []
    @hours = []
    @queue = (0...balls).to_a
    @balls = balls
  end

  def unique_cycles
    if @@memoized[@balls]
      return @@memoized[@balls]
    end
    cycle!
    cycles = 1
    transform = Hash[(0...@queue.size).zip(@queue)]
    until @queue.each_cons(2).all? { |x, y| x < y }
      @queue = @queue.map { |x| transform[x] }
      cycles += 1
    end
    @@memoized[@balls] = cycles / 2
  end

private

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
  end

end

if __FILE__ == $0

  mutex = Mutex.new
  queue = []
  output = ""

  reader = Thread.new {
    input = true
    while input
      mutex.synchronize {
        unless queue.empty?
          input = queue.shift
          if v != 0
            output << BallClock.new(input).unique_cycles
          end
        end
      }
    end
  }  
  
  while(gets)
    mutex.synchronize {
      queue << $_.chomp.to_i
    }
  end

  mutex.synchronize {
    queue << nil
  }

  reader.join
  puts "It's #{output}"

end
