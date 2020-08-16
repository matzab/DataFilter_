  The problem: sort medication labels from garbage files in folder structured as

  root folder
  |___ subfolder 1
  |    |___ file 1
  |    |___ file 2
  |    ...
  |    |___ file n
  |
  |___ subfolder 2
  |    |___ file 1
  |    |___ file 2
  |    ...
  |    |___ file n
  .
  .
  .
  |
  |__ subfolder n
  ...
  ..


  Root folder contained 12.635 subfolders
  Each subfolder had an XML (web page) file describing medication
  Each folder contained labels of medication, some contained more labels, depending on medication dosage (5 mg, 10 mg, 50 mg etc.)
  the folders also contained molecular structure and other images (graphs, bar charts, usage etc.)


  We considered 3 possible solutions:

  i. pick images based on their name. Pick images that contain keywords like label*, lbl* and exclude images with name like
  structure*, str*, struct* etc. The algorithm was not implemented due to names of the files being too arbitrary.

  ii. Image labels usually presented the largest file in the folder, therefore we could use largest file as the guideline to pick up files in the folder.
  We find the largest file in the folder, and we pick every other file that is 10% or less in size, because sometimes folder contained multiple labels with
  different dosage. This algorithm was not so successful because sometimes there were big images of medication instructions or pills itself.

  This approach found about 17.000 images, but ~30% were mis-picked, so it would take a long time to manually sort the false positives.



  iii. Since most of the labels contained barcode we could use algorithm that scans through images searching for vertical and horizontal lines.
  Each image was scanned at worst 4 times (each pas it was rotated by 45 degrees to search for barcode presence)
  Each image was in JPG format

  to sort about 61.000 files it took approx. 6 hours
  found < 10 corrupted files (useless JPG files)
  found cca. 12.500 files with barcode, 250 were falsely classified to contain barcode
