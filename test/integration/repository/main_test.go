package repository_test

import (
	"context"
	"os"
	"testing"

	"github.com/vini/costify-go/test/integration/testdata"
)

var sharedDB *testdata.SharedDatabaseTestSuite

func TestMain(m *testing.M) {
	ctx := context.Background()
	
	// Setup shared database
	var err error
	sharedDB, err = testdata.GetSharedDatabaseTestSuite(ctx)
	if err != nil {
		panic("failed to setup shared database: " + err.Error())
	}
	
	// Run tests
	code := m.Run()
	
	// Cleanup
	if err := sharedDB.Cleanup(ctx); err != nil {
		panic("failed to cleanup shared database: " + err.Error())
	}
	
	os.Exit(code)
}