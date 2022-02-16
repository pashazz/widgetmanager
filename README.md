This is a widget manager application, supporting the following operations:

* add widget
* remove widget
* list all widgets

## The Widget

The widget has the following fields:

* x
* y
* width
* height
* z
* lastUpdatedAt - update timestamp is assigned automatically

### z - zOrder

The widget's zOrder (z) is unique. If Z-index is not specified, the widget moves to foreground Foreground - max Z,
background - min Z.

If the existing z is specified when creating or updating widget, then the new widget shifts old widget(s) upwards.

1) Given - 1,2,3; New - 2; Result - 1,2,3,4; Explanation: 2 (->3 ) and 3(->4) has been shifted;
2) Given - 1,5,6; New - 2; Result - 1,2,5,6; Explanation: No one shifted;
3) Given - 1,2,4; New - 2; Result - 1,2,3,4; Explanation: Only 2(->3) has been shifted

## The Storage

You can use an in-memory or DB storage. The storage parameters are specified in `applications.yml` file - via Spring
profiles (`spring.profiles.active`)

The profiles are the following

* `db` - DB storage backed by the DB of choice (default: H2)
* `memory` - in-memory storage backed by ArrayList and HashMap

### REST API

The rest API is self-explanatory

### 1. Add widget

```http request
POST localhost:8080/widgets
Content-Type: application/json

{
  "x": 20,
  "y": 40,
  "width": 14,
  "height": 20
}
```

result

```json
{
  "id": 2,
  "x": 20,
  "y": 40,
  "z": 1,
  "width": 14,
  "height": 20,
  "lastUpdatedAt": "2022-02-07T04:08:51.56429"
}
```

### 2. Update widget

```http request
PUT localhost:8080/widgets/1
Content-Type: application/json

{
  "x": 25,
  "z": 5
}
```

result

```json
{
  "id": 1,
  "x": 25,
  "y": 40,
  "z": 5,
  "width": 14,
  "height": 20,
  "lastUpdatedAt": "2022-02-07T04:16:47.918387"
}
```

### 3. List all widgets - sorted by zOrder

```http request
GET http://localhost:8080/widgets/all
Accept: application/json

```

result

```
[
  {
    "id": 1,
    "x": 25,
    "y": 40,
    "z": 5,
    "width": 14,
    "height": 20,
    "lastUpdatedAt": "2022-02-07T04:16:47.918387"
  }
]

```

### 4. List widgets in pages

```http request
GET localhost:8080/widgets
Content-Type: application/json
{
  "page": 0,
  "size": 2
}
```

Default page size is 50

Result:

```json
[
  {
    "id": 1,
    "x": 25,
    "y": 40,
    "z": 5,
    "width": 14,
    "height": 20,
    "lastUpdatedAt": "2022-02-07T04:16:47.918387"
  },
  ...
]
```

### 5. Delete widget

```http request
DELETE localhost:8080/widgets/1
```

Result:

empty, code 200

### 6. Get widget by id

```http request
GET localhost:8080/widgets/2

```

result

```
{
  "id": 2,
  "x": 20,
  "y": 40,
  "z": 0,
  "width": 14,
  "height": 20,
  "lastUpdatedAt": "2022-02-07T04:22:18.784118"
}
```
