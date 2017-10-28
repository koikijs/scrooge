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
    "name": "Koiki Camp"    ... (required)
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
            "id": "507f1f77bcf86cd799439011",
            "memberName": "Nabnab",
            "paidAmount": 200,
            "forWhat": "rent-a-car",
            "createdAt": "2017-10-28T11:13:25Z",
            "updatedAt": "2017-10-28T11:13:25Z"
        }
    ],
    "aggPaidAmount": [
        {
            "memberName": "Nabnab",
            "totalPaidAmount": 200
        }
    ]
}
```

### POST /events/{eventId}/scrooges
```
{
    "memberName": "Nabnab", ... (required)
    "paidAmount": 200,      ... (required)
    "forWhat": "rent-a-car" ... (optional)
}
```

### PATCH /scrooges/{scroogeId}
```
{
    "memberName": "Nabnab", ... (required)
    "paidAmount": 200,      ... (required)
    "forWhat": "rent-a-car" ... (optional)
}
```

### DELETE /scrooges/{scroogeId}
No body

## WebSocket
This API sends broadcast to all users who have same eventId when resource is updated.

```
{
    “uri”: “/events”,
    “method”: “POST”,
    “body”: {
        "name": "Koiki Camp"
    }
}
```