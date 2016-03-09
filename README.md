# CATSImageDownloader
Thumbnail image downloader for Georgia Tech's Center for Accessible Technology in Sign (CATS) SMARTSignDictionary.
Run the compiled jar like so:
`java -jar CATSImageDownloader list-of-queries.txt export-directory`

Download the latest release:
https://github.com/petosa/CATSImageDownloader/releases

# Features
## Image Downloader
Given a text file of queries, download an iconic image for that word. Images are all photos greater than 1024 x 768. All photos are scraped from Google Images. Optionally, provide a category for each query to improve accuracy and for automatic sorting in the export directory. Text files should be formatted like so:
```
entrance     //With no categories specified, this query is in the default uncategorized category
smile        //Same for this query
@animal      //Apply animal category to the subsequent queries
axolotl
buffalo
@food        //Apply food category to the subsequent queries
dumpling
spring onion
@            //Reset category to uncategorized for subsequent queries
shoe laces
time
```
Notice how `axolotl` and `buffalo` are in the `animal` category, `dumpling` and `spring onion` are in the `food` category, and `entrance`, `smile`, `shoe laces`, and `time` are in the default uncategorized category.

![Alt text](http://i.imgur.com/cJSikDH.png "categories")


## Ambiguity Resolution
A lot of times, a query is ambiguous. For example, `crane` can either be an animal or can be used in `construction`. Such ambiguous queries are automatically detected. So long as you have set categories for ambiguous queries, the correct image will be downloaded.

`crane : animal`

![Alt text](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRaBFnG0WII2bkKZj-oyX-hvl19MzbZF4k8tPzm3OG-C5eE0GdY "crane : animal")

`crane : construction`

![Alt text](https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcSiv_Uia5zhGEFkeO3ovVckteH1XLFhBoxhtxnkkY_Hz05PfijG "crane : construction")

## Recursive Error Resolution
Some images just can't be downloaded due to 404's or 403's. To resolve this, another thread is started with instructions to download the subsequent image result. This redundancy more or less guarantees a result for each query.

## Multithreaded Downloads
Images for each query are downloaded concurrently.
