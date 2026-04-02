# TRADEOFFS.md

## Architecture
since there's no request and response cycle  and i don't need endpoint and port going for spring boot would  add unnecesssary dependencies and complexity to solving the problem .

i seperate concerns into different classes because i prefer each to adopt single responsibility so it can tested differently without disturbing or corrupting other files.

## Concurrency Strategy
using batching is for rate limiting and to ensure one never exceed it and dosen't  need to wait  for other request one at a time.

without it the app may crashed due to load of request mounting the server and more importantly the gemini api we are using has rate limit also which can also crashed the app also , so adopting batch sequencial process make it work better

## Error Handling
i retry when request to servers break , i do not retry when input is wrong from client, it is important because it help manage the application from crashing abruptly and if it is to crashed then it crashed gracefully. the request code for retry are 500+ and 429 which is for rate limiting and the request for not retry are the 400, 401 and 404 which is mainly for client error

## Checkpointing
the checkpoint solve resumable of app when crashed from where it stop , assuming you are processing thousands of tweet and it remain one tweet to complete but your app crashed , checkpoint would allow you to resume at the processed tweet  before the last tweet to sure of where to resume and would make you start afresh

## What I'd Do Differently
i could add better logging for observability, run more test to catch edge cases as much as i can and based on the project scope , adding more dependencies framework will likely complex stuff , i would stick to the plain java cli and also sequential batching is okay 