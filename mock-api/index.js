const express = require('express');
const app = express();
const port = 8081;

app.get('/api/score', (req, res) => {
    const eventId = req.query.eventId || "unknown";
    res.json({
        eventId: eventId,
        currentScore: `${Math.floor(Math.random() * 5)}:${Math.floor(Math.random() * 5)}`
    });
});

app.listen(port, () => {
    console.log(`Mock API running at http://localhost:${port}`);
});