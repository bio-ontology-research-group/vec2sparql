FROM python:3
RUN pip install --upgrade pip

WORKDIR /app

COPY . .

RUN pip install -r requirements.txt

ENTRYPOINT ["python", "/app/index.py"]