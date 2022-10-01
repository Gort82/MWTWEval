***********************************************************************************************
***********************************          SD-MW               ******************************
***********************************************************************************************
This is a partial implementation of the technique presented in the paper: Pérez Gort, M. L., 
Olliaro, M., Cortesi, A., & Uribe, C. F. (2021). Semantic-driven watermarking of relational 
textual databases. Expert Systems with Applications, 167, 114013.

1. Remember to generate all .class files after deploying the project. 
2. Assign the correct path to load Properties.XML (file WNCaller.java, line 34) 
3. Configure file Properties.XML (update reference to the dictionary)

Remember to use the same attributes we used for each data set for the experiments executed in 
the proposed manuscript. 
For the watermark extraction, instead of using the same attributes, use the columns having as 
prefix 'EMB_' + the names of the attributes selected for watermark embedding. 
This is performed to keep track of the number of marks embedded without requiring word 
replacement in the dataset. 

For the embedding:
1. First click the button '...', which is used to check the data contained and select 
the attributes for mark embedding according to the parameters of the pane 'Sentence Selection'.

2. Select the image used to generate the watermark.

3. Proceed with the watermark embedding. 

For the extraction:
1. First click the button '...', which is used to check the data contained and selòect the 
attributes for mark embedding according to the parameters of the pane 'Sentence Selection'.
2. Write the height and width of the image to generate from the extracted watermark signal.
3. Check that all parameter values are the same as the ones used previously to embed the 
watermark. 
4. Press the 'Extract' button. 
3. After the watermark extraction is concluded, press the 'Save' button, which saves the 
extracted image to then compute the metrics values on the main screen. 

To compute the values of the metrics in the screen 'Relational Textual Database Watermarking', 
press the button 'Calculate' (Remember that watermark extraction and embedding must be executed 
first to obtain values that make sense).
Also, on the same screen are the buttons to analyze the effects that operations such as tuple 
insertion, attribute updates, and tuple deletion will have on the detected watermark signal. 
Some of these operations are constrained fo the conditions defined in the proposed manuscript. 
In particular, when performing the updates of attributes (button 'UPDATE' of the main screen), 
when the data set 'TEX_DOCUMENTS' is selected to embed the watermark, the updates will be 
performed just on that attributes according to the percentage of tuples being involved. 
On the other hand, when selecting 'UNIVE_SYLLABUS' data set, the number of attributes to 
update will be restricted to a maximum of 4, considering the maximum number of attributes 
selected as carriers mentioned in the manuscript. 
***********************************************************************************************
***********************************          SD-MW               ******************************
***********************************************************************************************
_______________________________________________________________________________________________
_______________________________________________________________________________________________

***********************************************************************************************
***********************************          MA-NM               ******************************
***********************************************************************************************
This is a partial implementation of the technique presented in the paper: Pérez Gort, M. L., 
Feregrino Uribe, C., & Nummenmaa, J. (2017, June). A minimum distortion: High capacity 
watermarking technique for relational data. In Proceedings of the 5th ACM Workshop on Information 
Hiding and Multimedia Security (pp. 111-121).

Considering is a symmetric technique (same as SD-MW), parameters values have to be the same for 
both the embedding and extraction processes.

For this case, the only requirement for embedding is to select the binary image to generate the 
watermark. On the other hand, for the extraction process, the size of the image generated from 
the extracted watermark (height and width of the fields) must be written. After extracting the 
signal from the watermark, the 'Enhance' button should be pressed to improve the quality of the 
image created. Next, the 'Save' button should be clicked to save the images as files for SSIM 
calculation in the main window.

***********************************************************************************************
***********************************          MA-NM               ******************************
***********************************************************************************************