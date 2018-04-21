
var plotGraph = function(traces) {
	var graphDiv = 'graph';
	Plotly.purge(graphDiv);

	var colors = ['rgb(255,153,51)', 'grey', 'rgb(19,136,8)'];
	var legendLabels = ['Walk', 'Jump', 'Run']
	var plotData = [];

	traces.forEach(function(trace, i) {
		var label = i;
		console.log("label "+ label);
		var labColor = colors[trace['c'][0]]
		console.log("color :" + labColor);
		plotData.push({
			name: legendLabels[trace['c'][0]],
			type: 'scatter3d',
			mode: 'lines+markers',
			x: trace['x'],
			y: trace['y'],
			z: trace['z'],
			line: {
				width: 5,
				color: labColor
			},
			marker: {
				size: 3.5,
				color: trace['c'],
				colorscale: "rgb(100,100,100)",
				cmin: -20,
				cmax: 50
			}
		});
	});
	var layout = {
        showlegend: true,
        legend: {"orientation": "h"}
    };

	Plotly.plot(graphDiv, plotData, layout);
};

var parseData = function(traces, data, selectedLabels) {

	var colors = ['red', 'blue', 'green'];
	var x, y, z, c;
	data.forEach(function(dataEl) {
		x = [];
		y = [];
		z = [];
		c = [];

		dataEl.forEach(function(el) {
			if (selectedLabels.includes(el['c'])) {
				x.push(el['x']);
				y.push(el['y']);
				z.push(el['z']);
				c.push(el['c']);
			}
		});

		traces.push({
			"x": x,
			"y": y,
			"z": z,
			"c": c
		});
	});

};

$(function() {

    var d = Android.getData();

    $("#data").text("data:" + d);

    var data = JSON.parse(Android.getData());

	traces = [];
	var selectedLabels = [0, 1, 2];

	$('.chart-control').on('change', function(event) {
		var $el = $(this);
		var val = parseInt($el.val());

		if ($el.prop('checked') == true) {
			if (!selectedLabels.includes(val)) {
				selectedLabels.push(val);
			}
		} else {
			if (selectedLabels.includes(val)) {
				selectedLabels.splice(selectedLabels.indexOf(val), 1);
			}
		}
		traces = [];
		parseData(traces, data, selectedLabels);
		plotGraph(traces);
	});

	parseData(traces, data, selectedLabels);
	plotGraph(traces);
});
