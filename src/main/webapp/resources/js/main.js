(function () {
    var canvas = document.querySelector('canvas'),
        context = canvas.getContext('2d'),
        height = 400,
        width = 0;
        
    function initialize() {
        resize();
        update();
    }

    function resize() {
        canvas.style.width = '100%';
        canvas.width = width = canvas.offsetWidth;
        canvas.height = height;
    }

    function update() {
        requestAnimationFrame(update);

        step();
        clear();
        paint();
    }

    // Takes a step in the simulation
    function step() {
        
    }

    // Clears the painting
    function clear() {
        context.clearRect(0, 0, canvas.width, canvas.height);
    }

    // Paints the current state
    function paint() {
        context.fillStyle = '#000';
        context.fillRect(0, 0, width, height);   
    }

    initialize();
})();