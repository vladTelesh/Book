const div = document.getElementById($0);

if (div.hasChildNodes()) {
    while (div.firstChild) {
        div.removeChild(div.firstChild);
    }
}

const canvas = document.createElement('canvas');
canvas.setAttribute('id', 'line-chart');
canvas.setAttribute('height', '450');
canvas.setAttribute('width', '800');
div.appendChild(canvas);
const chart = new Chart(canvas, {
    type: 'line',
    data: {
        labels: $1.split(', '),
        datasets: [{
            data: $2.split(', '),
            label: "Number of read books",
            borderColor: $3,
            fill: false
        }
        ]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            yAxes: [
                {
                    id: 'y-axis-1',
                    type: 'linear',
                    display: true,
                    position: 'left',
                    ticks: {
                        beginAtZero: true,
                        precision: 0,
                        fixedStepSize: 1
                    }
                }
            ]
        }
    }
});