// [[array of run],[array of walk],[array of jump]]
trace1 = [];
trace2 = [];
trace3 = [];


for (var i = 0; i < 10; i++) {
	trace1.push({
		"x": Math.random() * 15,
		"y": Math.random() * 15,
		"z": Math.random() * 15,
		"c": 0
	})
}


for (var i = 0; i < 10; i++) {
	trace2.push({
		"x": Math.random() * 15,
		"y": Math.random() * 15,
		"z": Math.random() * 15,
		"c": 1
	})
}


for (var i = 0; i < 10; i++) {
	trace3.push({
		"x": Math.random() * 15,
		"y": Math.random() * 15,
		"z": Math.random() * 15,
		"c": 2
	})
}

window.data = [
	trace1, trace2, trace3
];
