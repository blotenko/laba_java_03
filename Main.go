package main

import (
	"fmt"
	"sync"
	"time"
)

var k int = 0 //  общий ресурс
func main() {
	var arr [10]int
	arr[0] = 2
	arr[1] = 3
	arr[2] = 1
	arr[3] = 3
	arr[4] = 3
	arr[5] = 2
	arr[6] = 2
	arr[7] = 1
	arr[8] = 0
	arr[9] = 0

	var wg sync.WaitGroup
	wg.Add(3) // в группе три горутины

	work := func(id int) {
		fmt.Printf("Горутина %d начала выполнение \n", id)
		var mutex sync.Mutex
		time.Sleep(1 * time.Second)
		defer wg.Done()
		for k < 8 {
			mutex.Lock()
			if arr[k] != id && arr[k+1] != id && arr[k] != 0 {
				println("Smoking ", id)
				k += 2
			}
			mutex.Unlock()

		}
	}

	go work(1)
	go work(2)
	go work(3)

	wg.Wait()
	fmt.Println("Горутины завершили выполнение")
}
