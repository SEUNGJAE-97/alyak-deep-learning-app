from pydantic import BaseModel, Field


class TrainRequest(BaseModel):
    jobId: str = Field(min_length=1)
    datasetStatus: str
    epochs: int
    batchSize: int
    learningRate: float
    optimizer: str
    freezeLayers: str | None = None
    baseModelPath: str | None = None
