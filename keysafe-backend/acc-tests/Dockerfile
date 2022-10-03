FROM python:3.10-alpine
RUN mkdir /app
ADD . /app
WORKDIR /app
RUN pip install requests
RUN pip install arrow
CMD ["python", "AccTests.py"]