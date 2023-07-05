package com.sunny.saf;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.database.Cursor;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.runtime.ActivityResultListener;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.errors.YailRuntimeError;
import com.google.appinventor.components.runtime.util.AsynchUtil;
import com.google.appinventor.components.runtime.util.YailList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/*
author :- Sunny Gupta (vknow360)
*/


public class SAF extends AndroidNonvisibleComponent implements ActivityResultListener {
  private final Activity activity;
  private final ContentResolver contentResolver;
  private int intentReqCode = 0;

  public SAF(ComponentContainer container) {
    super(container.$form());
    activity = container.$context();
    contentResolver = activity.getContentResolver();
  }

  @Override
  public void resultReturned(int requestCode, int resultCode, Intent intent) {
    if (intentReqCode == requestCode) {
      if (resultCode == Activity.RESULT_OK) {
        GotUri(intent.getData(), String.valueOf(intent.getData()));
      } else if (resultCode == Activity.RESULT_CANCELED) {
        GotUri("", "");
      }
    }
  }

  private int getIntentReqCode() {
    if (intentReqCode == 0) {
      this.intentReqCode = form.registerForActivityResult(this);
    }
    return intentReqCode;
  }

  private void postError(final String method, final String message) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ErrorOccurred(method, message);
      }
    });
  }

  @SimpleEvent(description = "Event indicating error/exception has occurred and returns origin method and error message.")
  public void ErrorOccurred(String methodName, String errorMessage) {
    EventDispatcher.dispatchEvent(this, "ErrorOccurred", methodName, errorMessage);
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
  @SimpleFunction(description="Returns uri which can be used as Initial Dir in SAF picker")
  public String InitialDir(String dir){
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
      StorageManager sm = (StorageManager) form.$context().getSystemService(Context.STORAGE_SERVICE);
      Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
      Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
      String scheme = uri.toString();
      scheme = scheme.replace("/root/", "/document/");
      scheme += "%3A" + dir.replaceAll("/","%2F");
      return String.valueOf(Uri.parse(scheme));
    }
    return "";
  }

  @SimpleFunction(description = "Prompts user to select a document tree")
  public void OpenDocumentTree(String title, String initialDir) {
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
    intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
    if (!initialDir.isEmpty()) {
      intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(initialDir));
    }
    activity.startActivityForResult(Intent.createChooser(intent, title), getIntentReqCode());
  }

  @SimpleFunction(description = "Prompts user to select a single file")
  public void OpenSingleDocument(String title, String initialDir, String type, YailList extraMimeTypes) {
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    if (!type.isEmpty()) {
      intent.setType(type);
    }
    if (!initialDir.isEmpty()) {
      intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(initialDir));
    }
    intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
    if (!extraMimeTypes.isEmpty()) {
      intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes.toStringArray());
    }
    activity.startActivityForResult(Intent.createChooser(intent, title), getIntentReqCode());
  }

  @SimpleFunction(description = "Take a persistable URI permission grant that has been offered. Once taken, the permission grant will be remembered across device reboots.")
  public void TakePersistableUriPermission(Object uri, int flags) {
    try {
      activity.getContentResolver().takePersistableUriPermission((Uri) uri, flags);
    } catch (Exception e) {
      postError("TakePersistableUriPermission", e.getMessage());
    }
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

  @SimpleFunction(description = "Returns display name of given document uri")
  public String GetDisplayName(final String documentUri) {
    try {
      return getStringValue(documentUri, DocumentsContract.Document.COLUMN_DISPLAY_NAME);
    } catch (Exception e) {
      postError("DisplayName", e.getMessage());
    }
    return "";
  }

  @SimpleFunction(description = "Returns size (in bytes) of given document uri")
  public String GetSize(final String documentUri) {
    try {
      return getStringValue(documentUri, DocumentsContract.Document.COLUMN_SIZE);
    } catch (Exception e) {
      postError("Size", e.getMessage());
    }
    return "";
  }

  @SimpleFunction(description = "Returns last modified time (epoch) of given document uri")
  public String GetLastModifiedTime(final String documentUri) {
    try {
      return getStringValue(documentUri, DocumentsContract.Document.COLUMN_LAST_MODIFIED);
    } catch (Exception e) {
      postError("LastModifiedTime", e.getMessage());
    }
    return "";
  }

  @SimpleFunction(description = "Returns mime type of given document uri")
  public String GetMimeType(final String documentUri) {
    try {
      return getStringValue(documentUri, DocumentsContract.Document.COLUMN_MIME_TYPE);
    } catch (Exception e) {
      postError("MimeType", e.getMessage());
    }
    return "";
  }

  private String getStringValue(String documentUri, String projection) throws Exception {
    Cursor cursor = activity.getContentResolver().query(Uri.parse(documentUri),
            new String[]{projection},
            null, null, null);
    try {
      if (cursor != null && cursor.moveToFirst()) {
        return cursor.getString(0);
      }
    } finally {
      cursor.close();
    }
    return "";
  }

  @SimpleFunction(description = "Returns whether document can be copied or not")
  public boolean IsCopySupported(final String documentUri) {
    return isFlagTrue("IsCopySupported",
            Uri.parse(documentUri),
            DocumentsContract.Document.FLAG_SUPPORTS_COPY);
  }

  @SimpleFunction(description = "Returns whether document is movable or not")
  public boolean IsMoveSupported(final String documentUri) {
    return isFlagTrue("IsMoveSupported",
            Uri.parse(documentUri),
            DocumentsContract.Document.FLAG_SUPPORTS_MOVE);
  }

  @SimpleFunction(description = "Returns whether document is deletable or not")
  public boolean IsDeleteSupported(final String documentUri) {
    return isFlagTrue("IsDeleteSupported",
            Uri.parse(documentUri),
            DocumentsContract.Document.FLAG_SUPPORTS_DELETE);
  }

  @SimpleFunction(description = "Returns whether document is deletable or not")
  public boolean IsRenameSupported(final String documentUri) {
    return isFlagTrue("IsRenameSupported",
            Uri.parse(documentUri),
            DocumentsContract.Document.FLAG_SUPPORTS_RENAME);
  }

  private boolean isFlagTrue(String method, Uri uri, int flag) {
    try {
      Cursor cursor = contentResolver.query(uri,
              new String[]{DocumentsContract.Document.COLUMN_FLAGS},
              null,
              null,
              null);
      try {
        if (cursor != null && cursor.moveToFirst()) {
          return cursor.getString(0).contains(String.valueOf(flag));
        }
      } finally {
        cursor.close();
      }
    } catch (Exception e) {
      postError(method, e.getMessage());
    }
    return false;
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

  @SimpleFunction(description = "Writes content as text to given uri")
  public void WriteToFile(final String uriString, final String content) {
    AsynchUtil.runAsynchronously(new Runnable() {
      @Override
      public void run() {
        if (!GetMimeType(uriString).equals(DocumentDirMimeType())) {
          String res;
          try {
            OutputStream fileOutputStream = contentResolver.openOutputStream(Uri.parse(uriString), "wt");
            res = writeToOutputStream(fileOutputStream, content);
            res = res.isEmpty() ? uriString : res;
          } catch (Exception e) {
            res = e.getMessage();
          }
          postWriteResult(res);
        } else {
          postError("WriteToFile", "Can't write text to dir");
        }
      }
    });
  }

  @SimpleFunction(description = "Writes content as HEX to given uri")
  public void WriteAsHexString(final String uriString, final String content) {
    AsynchUtil.runAsynchronously(new Runnable() {
      @Override
      public void run() {
        if (!GetMimeType(uriString).equals(DocumentDirMimeType())) {
          try {
            byte[] data = hexStringToByteArray(content);
            OutputStream outputStream = contentResolver.openOutputStream(Uri.parse(uriString), "wt");
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            arrayOutputStream.write(data);
            arrayOutputStream.writeTo(outputStream);
            postWriteResult(uriString);
          } catch (Exception e) {
            postWriteResult(e.getMessage());
          }
        } else {
          postError("WriteAsHexString", "Can't write text to dir");
        }
      }
    });
  }

  private byte[] hexStringToByteArray(String s) {
    s = s.replaceAll(" ", "");
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
              + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }

  private String writeToOutputStream(OutputStream fileOutputStream, String content) {
    OutputStreamWriter writer = null;
    try {
      writer = new OutputStreamWriter(fileOutputStream);
      writer.write(content);
    } catch (Exception e) {
      e.printStackTrace();
      return e.getMessage();
    } finally {
      try {
        if (writer != null) {
          writer.flush();
          writer.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return "";
  }

  @SimpleFunction(description = "Writes byte array to given document")
  public void WriteAsByteArray(final String uriString, final Object byteArray) {
    AsynchUtil.runAsynchronously(new Runnable() {
      @Override
      public void run() {
        if (!GetMimeType(uriString).equals(DocumentDirMimeType())) {
          try {
            OutputStream outputStream = contentResolver.openOutputStream(Uri.parse(uriString), "wt");
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            arrayOutputStream.write((byte[]) byteArray);
            arrayOutputStream.writeTo(outputStream);
            postWriteResult(uriString);
          } catch (Exception e) {
            postWriteResult(e.getMessage());
          }
        } else {
          postError("WriteAsByteArray", "Can't write bytes to dir");
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

  @SimpleFunction(description = "Tries to delete document from given uri and returns result")
  public boolean DeleteDocument(String uriString) {
    try {
      return DocumentsContract.deleteDocument(activity.getContentResolver(), Uri.parse(uriString));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new YailRuntimeError(e.getMessage(), "SAF");
    }
  }

  @SimpleFunction(description = "Reads from given document as text")
  public void ReadFromFile(final String uriString) {
    AsynchUtil.runAsynchronously(new Runnable() {
      @Override
      public void run() {
        if (!GetMimeType(uriString).equals(DocumentDirMimeType())) {
          String res;
          try {
            if (uriString.startsWith("//")) {
              InputStream is = form.getAssets().open(uriString.substring(2));
              res = readFromInputStream(is);
            } else {
              res = readFromInputStream(contentResolver.openInputStream(Uri.parse(uriString)));
            }
          } catch (Exception e) {
            res = e.getMessage();
          }
          postReadResult(res);
        } else {
          postError("ReadFromFile", "Can't read text from dir");
        }
      }
    });
  }

  private String readFromInputStream(InputStream fileInputStream) {
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
      return e.getMessage();
    } finally {
      try {
        input.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @SimpleFunction(description = "Reads content of document as byte array")
  public void ReadAsByteArray(final String uriString) {
    AsynchUtil.runAsynchronously(new Runnable() {
      @Override
      public void run() {
        if (!GetMimeType(uriString).equals(DocumentDirMimeType())) {
          try {
            InputStream inputStream;
            if (uriString.startsWith("//")) {
              inputStream = form.getAssets().open(uriString.substring(2));
            } else {
              inputStream = contentResolver.openInputStream(Uri.parse(uriString));
            }
            byte[] b = new byte[8192];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int c;
            while ((c = inputStream.read(b)) != -1) {
              os.write(b, 0, c);
            }
            inputStream.close();
            postReadResult(os.toByteArray());
          } catch (Exception e) {
            postReadResult(e.getMessage());
          }
        } else {
          postError("ReadAsByteArray", "Can't read bytes from dir");
        }
      }
    });
  }

  @SimpleFunction(description = "Reads content of document as HEX string")
  public void ReadAsHexString(final String uriString) {
    AsynchUtil.runAsynchronously(new Runnable() {
      @Override
      public void run() {
        if (!GetMimeType(uriString).equals(DocumentDirMimeType())) {
          try {
            InputStream inputStream;
            if (uriString.startsWith("//")) {
              inputStream = form.getAssets().open(uriString.substring(2));
            } else {
              inputStream = contentResolver.openInputStream(Uri.parse(uriString));
            }
            byte[] b = new byte[4096];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int c;
            while ((c = inputStream.read(b)) != -1) {
              os.write(b, 0, c);
            }
            inputStream.close();
            byte[] bytes = os.toByteArray();
            final String result = encodeHexString(bytes);
            postReadResult(result);
          } catch (Exception e) {
            e.printStackTrace();
            postReadResult(e.getMessage());
          }
        } else {
          postError("ReadAsHexString", "Can't read hex from dir");
        }
      }
    });
  }

  public String encodeHexString(byte[] bytes) {
    char[] HEXARRAY = "0123456789ABCDEF".toCharArray();
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEXARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEXARRAY[v & 0x0F];
    }
    return new String(hexChars);
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
    try {
      activity.getContentResolver().releasePersistableUriPermission(Uri.parse(uri), flags);
    } catch (Exception e) {
      postError("ReleasePermission", e.getMessage());
    }
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
        boolean successful = true;
        String response = "";
        try {
          response = DocumentsContract.copyDocument(contentResolver,
                  Uri.parse(sourceUri),
                  Uri.parse(targetParentUri)).toString();
        } catch (Exception e) {
          successful = false;
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
        String response;
        boolean successful = true;
        try {
          response = DocumentsContract.moveDocument(contentResolver,
                  Uri.parse(sourceUri),
                  Uri.parse(sourceParentUri),
                  Uri.parse(targetParentUri)).toString();
        } catch (Exception e) {
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
      return DocumentsContract.renameDocument(contentResolver,
              Uri.parse(documentUri),
              displayName).toString();
    } catch (FileNotFoundException e) {
      postError("RenameDocument", e.getMessage());
      return "";
    }
  }

  @SimpleFunction(description = "Tries to copy document from source uri to ASD")
  public void CopyDocumentToASD(final String sourceUri) {
    AsynchUtil.runAsynchronously(new Runnable() {
      @Override
      public void run() {
        String name = GetDisplayName(sourceUri);
        File file = new File(activity.getExternalFilesDir(null), name);
        try {
          FileOutputStream fos = new FileOutputStream(file);
          InputStream is = contentResolver.openInputStream(Uri.parse(sourceUri));
          byte[] buffers = new byte[4096];
          int read;
          while ((read = is.read(buffers)) != -1) {
            fos.write(buffers, 0, read);
          }
          is.close();
          fos.close();
          postCtoASDresult(true, file.getPath());
        } catch (Exception e) {
          e.printStackTrace();
          postCtoASDresult(false, e.getMessage());
        }
      }
    });
  }

  private void postCtoASDresult(final boolean successful, final String response) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        DocumentCopiedToASD(successful, response);
      }
    });
  }

  @SimpleEvent(description = "Event raised after getting 'CopyDocumentToASD' result")
  public void DocumentCopiedToASD(boolean successful, String response) {
    EventDispatcher.dispatchEvent(this, "DocumentCopiedToASD", successful, response);
  }

  @SimpleFunction(description="Converts text to bytes and return Byte Array object. It is used with 'WriteAsByteArray' method.")
  public Object ConvertStringToBytes(String byteString) {
    String[] str = byteString.substring(1, byteString.length() - 1).split(" ");
    List<String> list = new ArrayList<>();
    for (String s:
            str) {
      if (!s.trim().isEmpty()){
        list.add(s.trim());
      }
    }
    byte[] bytes = new byte[list.size()];
    for (int i = 0; i < list.size(); i++) {
      bytes[i] = (byte) Integer.parseInt(list.get(i));
    }
    return bytes;
  }

}