from fastapi import FastAPI
from pydantic import BaseModel
from typing import Dict, Any

app = FastAPI(
    title="Revive Mesh Intelligence Service",
    description="ML and Policy Intelligence for Revive Mesh",
    version="1.0.0"
)

@app.get("/health")
def health_check() -> Dict[str, Any]:
    """Health check endpoint for container probes."""
    return {"status": "ok", "service": "intelligence"}
