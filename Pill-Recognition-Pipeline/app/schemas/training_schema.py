from pydantic import BaseModel


class TrainRequest(BaseModel):
    datasetStatus: str
    epochs: int
    batchSize: int
    learningRate: float
    optimizer: str
    freezeLayers: str | None = None
    baseModelPath: str | None = None
