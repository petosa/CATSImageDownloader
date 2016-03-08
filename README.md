# CATSImageDownloader
Thumbnail image downloader for Georgia Tech's Center for Accessible Technology in Sign (CATS) SMARTSignDictionary 
Run the compiled jar like so:
`java -jar CATSImageDownloader list-of-queries.txt export-directory`

# Features
## Image Downloader
Given a text file of queries, download an iconic image for that word. Images are all photos greater than 1024 x 768. Optionally, provide a category for each query to improve accuracy and for automatic sorting in the export directory. Text files should be formatted like so:
```
dog : animal
ice cream : food
electricity
```
Notice how `electricity` does not have a category.

## Ambiguity Resolution
A lot of times, a query is ambiguous. For example, `crane` can either be an animal or can be used in `construction`. Such ambiguous queries are automatically detected. So long as you have set categories for ambiguous queries, the correct image will be downloaded.

`crane : animal`

![Alt text](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRaBFnG0WII2bkKZj-oyX-hvl19MzbZF4k8tPzm3OG-C5eE0GdY "crane : animal")

`crane : construction`

![Alt text](https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcSiv_Uia5zhGEFkeO3ovVckteH1XLFhBoxhtxnkkY_Hz05PfijG "crane : construction")
