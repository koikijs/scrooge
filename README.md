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
request
```
{
    "name": "Koiki Camp"    ... (required)
}
```

### GET /events/{eventId}
response
```
{
    "name": "Koiki Camp",
    "id": "5a226c2d7c245e14f33fc5a8",
    "createdAt": "2017-12-02T16:52:45.52",
    "updatedAt": "2017-12-02T16:52:45.52",
    "scrooges": [
        {
            "memberName": "Nabnab",
            "paidAmount": 200,
            "forWhat": "rent-a-car",
            "id": "5a226c2d7c245e14f33fc5a8",
            "eventId": "5a226c2d7c245e14f33fc5a8",
            "createdAt": "2017-12-02T16:52:45.52",
            "updatedAt": "2017-12-02T16:52:45.52"
        },
        {
            "memberName": "Ninja",
            "paidAmount": 500,
            "forWhat": "beef",
            "id": "5a226c2d7c245e14f33fc5a8",
            "eventId": "5a226c2d7c245e14f33fc5a8",
            "createdAt": "2017-12-02T16:52:45.52",
            "updatedAt": "2017-12-02T16:52:45.52"
        }
    ],
    "transferAmounts": [
        {
            "from": "Nabnab",
            "to": "Ninja",
            "amount": 150
        }
    ],
    "aggPaidAmount": []
}
```

### POST /events/{eventId}/scrooges
request
```
{
    "memberName": "Nabnab", ... (required)
    "paidAmount": 200,      ... (required)
    "forWhat": "rent-a-car" ... (optional)
}
```
### GET /events/{eventId}/scrooges/{scroogeId}
response
```
{
    "memberName": "Nabnab",
    "paidAmount": 200,
    "forWhat": "rent-a-car",
    "id": "5a226c2d7c245e14f33fc5a8",
    "eventId": "5a226c2d7c245e14f33fc5a8",
    "createdAt": "2017-12-02T16:52:45.52",
    "updatedAt": "2017-12-02T16:52:45.52"
}
```

### DELETE /events/{eventId}/scrooges/{scroogeId}
No request body

### DELETE /events/{eventId}/scrooges?memberNames={memberName},{memberName}
No request body

## WebSocket
This API sends broadcast to all users who have same eventId when resource is updated.
