package com.sunny.saf;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.widget.ImageView;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.errors.YailRuntimeError;
import com.google.appinventor.components.runtime.util.AsynchUtil;
import com.google.appinventor.components.runtime.util.YailList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
author :- Sunny Gupta (vknow360)
*/

@DesignerComponent(version = 1,
        versionName = "1.1",
        description = "A non-visible component to access files using Storage Access Framework",
        category = ComponentCategory.EXTENSION,
        nonVisible = true,
        androidMinSdk = 21,
        iconName = "https://res.cloudinary.com/andromedaviewflyvipul/image/upload/c_scale,h_20,w_20/v1571472765/ktvu4bapylsvnykoyhdm.png")
@SimpleObject(external = true)
public class SAF extends AndroidNonvisibleComponent implements ActivityResultListener {
    private final Activity activity;
    private int intentReqCode = 0;

    public SAF(ComponentContainer container) {
        super(container.$form());
        activity = container.$context();
    }

    @SimpleProperty(description = "Returns mime type of document dir")
    public String DocumentDirMimeType() {
        return DocumentsContract.Document.MIME_TYPE_DIR;
    }

    @SimpleProperty(description = "Flag to get write permission")
    public int FlagGrantReadPermission() {
        return Intent.FLAG_GRANT_READ_URI_PERMISSION;
    }

    @SimpleProperty(description = "Flag to get read permission")
    public int FlagGrantWritePermission() {
        return Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
    }

    @SimpleFunction(description = "Combines two flags and returns resulting flag")
    public int CreateFlags(int f1, int f2) {
        return f1 | f2;
    }

    @SimpleFunction(description = "Convert uri to string")
    public String StringFromUriObject(Object uri) {
        return ((Uri) uri).toString();
    }

    @SimpleFunction(description = "Converts string to uri")
    public Object StringToUriObject(String uriString) {
        return Uri.parse(uriString);
    }

    @SimpleFunction(description = "Prompts user to select a document tree")
    public void OpenDocumentTree(String title,String initialDir) {
        if (intentReqCode == 0) {
            intentReqCode = form.registerForActivityResult(this);
        }
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        if (!initialDir.isEmpty()) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(initialDir));
        }
        activity.startActivityForResult(Intent.createChooser(intent,title), intentReqCode);
    }
    @SimpleFunction(description = "Prompts user to select a single file")
    public void OpenSingleDocument(String title, String category, String type, YailList extraMimeTypes){
        if (intentReqCode == 0) {
            intentReqCode = form.registerForActivityResult(this);
        }
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        if (!category.isEmpty()) {
            intent.addCategory(category);
        }
        if (!type.isEmpty()) {
            intent.setType(type);
        }
        intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        if (!extraMimeTypes.isEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES,extraMimeTypes.toStringArray());
        }
        activity.startActivityForResult(Intent.createChooser(intent,title), intentReqCode);
    }

    @SimpleFunction(description = "Take a persistable URI permission grant that has been offered. Once taken, the permission grant will be remembered across device reboots.")
    public void TakePersistableUriPermission(Object uri, int flags) {
        activity.getContentResolver().takePersistableUriPermission((Uri) uri, flags);
    }

    @SimpleFunction(description = "Returns whether given uri is a tree uri")
    public boolean IsTreeUri(String uriString) {
        return DocumentsContract.isTreeUri(Uri.parse(uriString));
    }

    @SimpleFunction(description = "Returns whether given uri is a document uri")
    public boolean IsDocumentUri(String uriString) {
        return DocumentsContract.isDocumentUri(activity, Uri.parse(uriString));
    }

    @SimpleFunction(description = "Returns whether second uri is child of first uri")
    public boolean IsChildDocumentUri(String parentUri, String childUri) {
        try {
            return DocumentsContract.isChildDocument(activity.getContentResolver(),
                    Uri.parse(parentUri),
                    Uri.parse(childUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new YailRuntimeError(e.getMessage(), "SAF");
        }
    }

    @SimpleFunction(description = "Returns document id of tree uri (should be either tree uri itself or a direct child uri)")
    public String GetTreeDocumentId(String uriString) {
        return DocumentsContract.getTreeDocumentId(Uri.parse(uriString));
    }

    @SimpleFunction(description = "Returns document id of an uri (should only be grand child)")
    public String GetDocumentId(String uriString) {
        return DocumentsContract.getDocumentId(Uri.parse(uriString));
    }

    @SimpleFunction(description = "Builds document uri using tree uri and document id")
    public String BuildDocumentUriUsingTree(String treeUri, String documentId) {
        return DocumentsContract.buildDocumentUriUsingTree(Uri.parse(treeUri), documentId).toString();
    }

    @SimpleFunction(description = "Builds child documents id using tree (documents which is child of parent document) uri and its parent document id")
    public String BuildChildDocumentsUriUsingTree(String treeUri, String parentDocumentId) {
        return DocumentsContract.buildChildDocumentsUriUsingTree(Uri.parse(treeUri), parentDocumentId).toString();
    }

    @SimpleFunction(description = "Returns mime type of given document uri")
    public String GetMimeType(final String documentUri) {
        try (Cursor cursor = activity.getContentResolver().query(Uri.parse(documentUri),
                new String[]{DocumentsContract.Document.COLUMN_MIME_TYPE},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return "";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    @SimpleFunction()
    public String IsCopySupported(final String documentUri){
        try (Cursor cursor = activity.getContentResolver().query(Uri.parse(documentUri),
                new String[]{String.valueOf(DocumentsContract.Document.FLAG_SUPPORTS_COPY)},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return "false";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    @SimpleFunction()
    public String IsMoveSupported(final String documentUri){
        try (Cursor cursor = activity.getContentResolver().query(Uri.parse(documentUri),
                new String[]{String.valueOf(DocumentsContract.Document.FLAG_SUPPORTS_MOVE)},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return "false";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    @SimpleFunction()
    public String IsDeleteSupported(final String documentUri){
        try (Cursor cursor = activity.getContentResolver().query(Uri.parse(documentUri),
                new String[]{String.valueOf(DocumentsContract.Document.FLAG_SUPPORTS_DELETE)},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return "false";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    @SimpleFunction()
    public String IsRenameSupported(final String documentUri){
        try (Cursor cursor = activity.getContentResolver().query(Uri.parse(documentUri),
                new String[]{String.valueOf(DocumentsContract.Document.FLAG_SUPPORTS_RENAME)},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return "false";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @SimpleFunction(description = "Returns display name of given document uri")
    public String GetDisplayName(final String documentUri) {
        try (Cursor cursor = activity.getContentResolver().query(Uri.parse(documentUri),
                new String[]{DocumentsContract.Document.COLUMN_DISPLAY_NAME},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return "";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @SimpleFunction(description = "Returns size (in bytes) of given document uri")
    public String GetSize(final String documentUri) {
        try (Cursor cursor = activity.getContentResolver().query(Uri.parse(documentUri),
                new String[]{DocumentsContract.Document.COLUMN_SIZE},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return "";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @SimpleFunction(description = "Returns last modified time (epoch) of given document uri")
    public String GetLastModifiedTime(final String documentUri) {
        try (Cursor cursor = activity.getContentResolver().query(Uri.parse(documentUri),
                new String[]{DocumentsContract.Document.COLUMN_LAST_MODIFIED},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return "";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @SimpleFunction(description = "Creates a new and empty document.If document already exists then an incremental value will be suffixed.")
    public void CreateDocument(final String parentDocumentUri, final String fileName, final String mimeType) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                try {
                    final String uri = DocumentsContract.createDocument(activity.getContentResolver(), Uri.parse(parentDocumentUri), mimeType, fileName).toString();
                    postCreateResult(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                    postCreateResult(e.getMessage());
                }
            }
        });
    }

    private void postCreateResult(final String uriString) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DocumentCreated(uriString);
            }
        });
    }

    @SimpleEvent(description = "Event invoked after creating document.Returns document's uri if operation was successful else returns error message")
    public void DocumentCreated(String uriString) {
        EventDispatcher.dispatchEvent(this, "DocumentCreated", uriString);
    }

    @SimpleFunction(description = "Writes to given uri")
    public void WriteToFile(final String uriString, final String content) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                String res = "";
                OutputStream fileOutputStream;
                try {
                    /*
                    ParcelFileDescriptor pfd = activity.getContentResolver().
                            openFileDescriptor(Uri.parse(uriString), "w");
                    FileOutputStream fileOutputStream =
                            new FileOutputStream(pfd.getFileDescriptor());
                    byte[] data = content.getBytes("UTF-8");
                    fileOutputStream.write(data);
                    fileOutputStream.close();
                    pfd.close();
                    */
                    fileOutputStream =
                            activity.getContentResolver().openOutputStream(Uri.parse(uriString));
                    res = writeToOutputStream(fileOutputStream,content);
                    res = res.isEmpty()?uriString:res;
                } catch (Exception e) {
                    e.printStackTrace();
                    res = e.getMessage();
                }
                postWriteResult(res);
            }
        });
    }
    private String writeToOutputStream(OutputStream fileOutputStream,String content){
        OutputStreamWriter writer = null;
        try{
            writer = new OutputStreamWriter(fileOutputStream);
            writer.write(content);
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @SimpleFunction
    public void WriteAsByteArray(final String uriString, final Object byteArray){
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream outputStream = activity.getContentResolver().openOutputStream(Uri.parse(uriString));
                    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                    arrayOutputStream.write((byte[])byteArray);
                    arrayOutputStream.writeTo(outputStream);
                    postWriteResult(uriString);
                }catch (Exception e){
                    e.printStackTrace();
                    postWriteResult(e.getMessage());
                }
            }
        });
    }

    private void postWriteResult(final String response) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GotWriteResult(response);
            }
        });
    }

    @SimpleEvent(description = "Event invoked after writing to document.Returns document's uri if operation was successful else returns error message")
    public void GotWriteResult(String response) {
        EventDispatcher.dispatchEvent(this, "GotWriteResult", response);
    }
    @SimpleFunction()
    public void SaveImageToDocumentUri(final Image image,final String uriString,final String format,final int quality){
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = ((BitmapDrawable)((ImageView)image.getView()).getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.valueOf(format.toUpperCase()),
                            quality,
                            activity.getContentResolver().openOutputStream(Uri.parse(uriString)));
                    postSaveImgResult(uriString);
                }catch (Exception e){
                    e.printStackTrace();
                    postSaveImgResult(e.getMessage());
                }
            }
        });
    }

    private void postSaveImgResult(final String res){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GotSaveImageResult(res);
            }
        });
    }
    @SimpleEvent()
    public void GotSaveImageResult(String response){
        EventDispatcher.dispatchEvent(this,"GotSaveImageResult",response);
    }

    @SimpleFunction(description = "Tries to delete document from given uri and returns result")
    public boolean DeleteDocument(String uriString) {
        try {
            return DocumentsContract.deleteDocument(activity.getContentResolver(), Uri.parse(uriString));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new YailRuntimeError(e.getMessage(), "SAF");
        }
    }

    @SimpleFunction(description = "Reads from given uri")
    public void ReadFromFile(final String uriString) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                String res = "";
                try {
                    res = readFromInputStream(activity.getContentResolver().openInputStream(Uri.parse(uriString)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                postReadResult(res);
            }
        });
    }

    private String readFromInputStream(InputStream fileInputStream){
        InputStreamReader input = new InputStreamReader(fileInputStream);
        try {
            StringWriter output = new StringWriter();
            int BUFFER_LENGTH = 4096;
            char[] buffer = new char[BUFFER_LENGTH];
            int offset = 0;
            int length;
            while ((length = input.read(buffer, offset, BUFFER_LENGTH)) > 0) {
                output.write(buffer, 0, length);
            }
            return normalizeNewLines(output.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }finally {
            try {
                input.close();
                /*
                fileInputStream.close();
                 */
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @SimpleFunction()
    public void ReadAsByteArray(final String uriString){
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                try{
                    InputStream inputStream = activity.getContentResolver().openInputStream(Uri.parse(uriString));
                    byte[] byteArray = new byte[Integer.parseInt(GetSize(uriString))];
                    inputStream.read(byteArray);
                    inputStream.close();
                    postReadResult(byteArray);
                }catch (Exception e){
                    e.printStackTrace();
                    postReadResult(e.getMessage());
                }
            }
        });
    }

    @SimpleEvent(description = "Event invoked after reading from document.Returns content if operation was successful else returns error message")
    public void GotReadResult(Object result) {
        EventDispatcher.dispatchEvent(this, "GotReadResult", result);
    }

    private void postReadResult(final Object r) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GotReadResult(r);
            }
        });
    }

    private String normalizeNewLines(String s) {
        return s.replaceAll("\r\n", "\n");
    }

    @SimpleFunction(description = "Returns whether read is available for given uri")
    public boolean IsReadGranted(String uri) {
        for (UriPermission uri1 : activity.getContentResolver().getPersistedUriPermissions()) {
            String str = uri1.getUri().toString();
            if (uri.equalsIgnoreCase(str)) {
                return uri1.isReadPermission();
            }
        }
        return false;
    }

    @SimpleFunction(description = "Relinquish a persisted URI permission grant")
    public void ReleasePermission(String uri, int flags) {
        activity.getContentResolver().releasePersistableUriPermission(Uri.parse(uri), flags);
    }

    @SimpleFunction(description = "Returns whether write is available for given uri")
    public boolean IsWriteGranted(String uri) {
        for (UriPermission uri1 : activity.getContentResolver().getPersistedUriPermissions()) {
            String str = uri1.getUri().toString();
            if (uri.equalsIgnoreCase(str)) {
                return uri1.isWritePermission();
            }
        }
        return false;
    }

    @SimpleEvent(description = "Event invoked when user selects a document or tree from SAF file picker")
    public void GotUri(Object uri, String uriString) {
        EventDispatcher.dispatchEvent(this, "GotUri", uri, uriString);
    }

    @SimpleFunction(description = "Tries to list files from given dir")
    public void ListFiles(final String dirUri, final String dirDocumentId) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                final List<String> list = listFiles(activity, Uri.parse(dirUri), dirDocumentId);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GotFilesList(list);
                    }
                });
            }
        });
    }

    // taken from https://stackoverflow.com/questions/41096332/issues-traversing-through-directory-hierarchy-with-android-storage-access-framew
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private List<String> listFiles(Context context, Uri uriTree, String documentId) {
        List<String> uriList = new ArrayList<>();
        Uri uriFolder = DocumentsContract.buildChildDocumentsUriUsingTree(uriTree, documentId);
        try (Cursor cursor = context.getContentResolver().query(uriFolder,
                new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                Uri uriFile = DocumentsContract.buildDocumentUriUsingTree(uriTree, cursor.getString(0));
                uriList.add(uriFile.toString());
                while (cursor.moveToNext()) {
                    uriFile = DocumentsContract.buildDocumentUriUsingTree(uriTree, cursor.getString(0));
                    uriList.add(uriFile.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new YailRuntimeError(e.getMessage(), "SAF");
        }
        return uriList;
    }

    @SimpleEvent(description = "Event invoked after getting files list")
    public void GotFilesList(List<String> filesList) {
        EventDispatcher.dispatchEvent(this, "GotFilesList", filesList);
    }

    @SimpleFunction(description = "Tries to copy document from source uri to target dir")
    public void CopyDocument(final String sourceUri, final String targetParentUri) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                boolean successful = false;
                String response = targetParentUri;
                try {
                    DocumentsContract.copyDocument(activity.getContentResolver(), Uri.parse(sourceUri), Uri.parse(targetParentUri));
                    successful = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    response = e.getMessage();
                }
                postCopyResult(successful, response);
            }
        });
    }

    private void postCopyResult(final boolean successful, final String response) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GotCopyResult(successful, response);
            }
        });
    }

    @SimpleEvent(description = "Event invoked after getting copy document result.Response will be target document's uri if operation was successful else returns error message")
    public void GotCopyResult(boolean successful, String response) {
        EventDispatcher.dispatchEvent(this, "GotCopyResult", successful, response);
    }

    @SimpleFunction(description = "Tries to move document from source uri to target dir")
    public void MoveDocument(final String sourceUri, final String sourceParentUri, final String targetParentUri) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                boolean successful = true;
                String response = targetParentUri;
                try {
                    DocumentsContract.moveDocument(activity.getContentResolver(), Uri.parse(sourceUri), Uri.parse(sourceParentUri), Uri.parse(targetParentUri));
                } catch (Exception e) {
                    e.printStackTrace();
                    successful = false;
                    response = e.getMessage();
                }
                postMoveResult(successful, response);
            }
        });
    }

    private void postMoveResult(final boolean successful, final String response) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GotMoveResult(successful, response);
            }
        });
    }

    @SimpleEvent(description = "Event invoked after getting move document result.Response will be target document's uri if operation was successful else returns error message")
    public void GotMoveResult(boolean successful, String response) {
        EventDispatcher.dispatchEvent(this, "GotMoveResult", successful, response);
    }

    @SimpleFunction(description = "Tries to rename a document and returns updated uri")
    public String RenameDocument(final String documentUri, final String displayName) {
        try {
            return DocumentsContract.renameDocument(activity.getContentResolver(), Uri.parse(documentUri), displayName).toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    public void resultReturned(int requestCode, int resultCode, Intent intent) {
        if (intentReqCode == requestCode) {
            GotUri(intent.getData(), intent.getData().toString());
        }
    }
}
