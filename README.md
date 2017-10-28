# Scrooge API
## Resource
### event
```
{
    "_id": "53be9d74b7fe1603319861e8",
    "name": "Koiki Camp",
    "createdAt": "2017-10-28T11:13:25Z",
    "updatedAt": "2017-10-28T11:13:25Z"
}
```
### scrooge
```
{
    "_id": "507f1f77bcf86cd799439011",
    "eventId": "53be9d74b7fe1603319861e8",
    "memberName": "Nabnab",
    "paidAmount": 200,
    "forWhat": "rent-a-car",
    "createdAt": "2017-10-28T11:13:25Z",
    "updatedAt": "2017-10-28T11:13:25Z"
}
```
## Endpoint
### POST /events
```
{
    "name": "Koiki Camp",
    "createdAt": "2017-10-28T11:13:25Z",
    "updatedAt": "2017-10-28T11:13:25Z"
}
```

### GET /events/{eventId}
```
{
    "name": "Koiki Camp",
    "createdAt": "2017-10-28T11:13:25Z",
    "updatedAt": "2017-10-28T11:13:25Z",
    "scrooges": [
        {
            "memberName": "Nabnab",
            "paidAmount": 200,
            "forWhat": "rent-a-car",
            "createdAt": "2017-10-28T11:13:25Z",
            "updatedAt": "2017-10-28T11:13:25Z"
        }
    ]
}
```

### POST /events/{eventId}/scrooges
```
{
    "memberName": "Nabnab",
    "paidAmount": 200,
    "forWhat": "rent-a-car",
    "createdAt": "2017-10-28T11:13:25Z",
    "updatedAt": "2017-10-28T11:13:25Z"
}
```

### PUT /events/{eventId}/scrooges/{scroogeId}
```
{
    "memberName": "Nabnab",
    "paidAmount": 200,
    "forWhat": "rent-a-car",
    "createdAt": "2017-10-28T11:13:25Z",
    "updatedAt": "2017-10-28T11:13:25Z"
}
```

## WebSocket
