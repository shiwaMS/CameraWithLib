using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using UnityEngine;
using UnityEngine.UI;

public class PluginWrapper : MonoBehaviour
{
    public RawImage display;
    public AspectRatioFitter fit;

    private Texture2D texture2D;
    private Int32 texturePtr;

    // Use this for initialization
    void Start()
    {
        AndroidJavaClass plugin = new AndroidJavaClass("microsoft.prototype.broadcastsupport.ImageLoader");
        this.texturePtr = plugin.CallStatic<Int32>("loadTextureId");
        Debug.Log("texture pointer = " + this.texturePtr);

        this.texture2D = new Texture2D(2, 2);

        if (texturePtr > 0)
        {
            Texture2D nativeTexture = Texture2D.CreateExternalTexture(144, 144, TextureFormat.ARGB32, false, true, new IntPtr(this.texturePtr));
            if (nativeTexture == null)
            {
                Debug.Log("nativeTexture object is null ");
                return;
            }
            else
            {
                Debug.Log("nativeTexture pixel length: " + nativeTexture.GetPixels().Length);
                Debug.Log("nativeTexture width x height: " + nativeTexture.width + " x " + nativeTexture.height);

                this.texture2D.UpdateExternalTexture(nativeTexture.GetNativeTexturePtr());
                Debug.Log("texture2D width x height: " + this.texture2D.width + " x " + this.texture2D.height);

                display.texture = this.texture2D;

                float ratio = 144.0f / 144.0f;
                fit.aspectRatio = ratio;
                Debug.Log("texture ratio = " + ratio);
            }
        }
    }

    void Update()
    { }
}
